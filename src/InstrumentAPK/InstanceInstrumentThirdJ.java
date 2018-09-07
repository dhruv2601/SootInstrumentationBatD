package InstrumentAPK;

import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.options.Options;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

public class InstanceInstrumentThirdJ {

    public static final String RESULTFILE = "/media/dhruv2601/Carseat/Top500FreeAppDataset/result/";
//    public static final String RESULTFILE = "/media/dhruv2601/Carseat/Top500FreeAppDataset/result/";
    public static int count = 0;
    public static final String APK_DIR = "/media/dhruv2601/Carseat/Top500FreeAppDataset/data_extend/";

    public static final Map<SootMethod, JimpleBody> jimpleBodyCache = new HashMap<SootMethod, JimpleBody>();
    public static String apk_path = "";

    public static List<String> mStartService = new ArrayList<>();
    public static List<String> mStopService = new ArrayList<>();
    public static List<String> mStartForeground = new ArrayList<>();
    public static List<String> mStopForeground = new ArrayList<>();

    static int sizeStartService = mStartService.size();
    static int sizeStopService = mStopService.size();
    static int sizeStartForeground = mStartForeground.size();
    static int sizeStopForeground = mStopForeground.size();

    static int c1 = 0, c2 = 0;

    public static void main(String[] args) {

        try {
            BufferedReader br = new BufferedReader(new FileReader("/media/dhruv2601/Carseat/Top500FreeAppDataset/app_ranking_500.txt"));
            String line = br.readLine();

            while (line != null) {
                apk_path = "";
                apk_path += APK_DIR + line;
                System.out.println("APK Path: " + apk_path);

        final List<String> logging = new ArrayList<>();
        final List<String> potentMethods = new ArrayList<>();

        mStartService.clear();
        mStopService.clear();
        mStartForeground.clear();
        mStopForeground.clear();

        mStartService.add("startService(android.content.Intent)|android.content.ContextWrapper");
        mStartService.add("startService(android.content.Intent)|android.app.Service");
        mStopService.add("stopService(android.content.Intent)|android.content.ContextWrapper");
        mStopService.add("stopService(android.content.Intent)|android.app.Service");
        mStartForeground.add("startForeground(int,android.app.Notification)|android.app.Service");
        mStopForeground.add("stopForeground(int)|android.app.Service");
        mStopForeground.add("stopForeground(boolean)|android.app.Service");

        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_src_prec(Options.src_prec_apk);

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);

        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);

        int iter = 0;

        while ((sizeStartService < mStartService.size()) || (sizeStopService < mStopService.size())
                || (sizeStartForeground < mStartForeground.size()) || (sizeStopForeground < mStopForeground.size())) {

            iter++;

            sizeStartService = mStartService.size();
            sizeStartForeground = mStartForeground.size();
            sizeStopService = mStopService.size();
            sizeStopForeground = mStopForeground.size();

            soot.G.reset();

            Options.v().set_output_format(Options.output_format_none);
            Options.v().set_src_prec(Options.src_prec_apk);

            Options.v().set_allow_phantom_refs(true);
            Options.v().set_whole_program(true);

            Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
            Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);

            count = 0;

            PackManager.v().getPack("jtp").add(new Transform("jtp.MyInstrumentor", new BodyTransformer() {
                @Override
                protected void internalTransform(Body body, String s, Map<String, String> map) {
                    final PatchingChain<Unit> units = body.getUnits();

                    if (count == 0) {
                        Iterator iterator = Scene.v().getClasses().snapshotIterator();
//                        Iterator iterator = Scene.v().getApplicationClasses().snapshotIterator();
//                        potentMethods.add(String.valueOf("Application classes total "+Scene.v().getApplicationClasses().size()));
//                        potentMethods.add(String.valueOf("Total Classes: "+Scene.v().getClasses().size()));
                        while (iterator.hasNext()) {
                            logging.clear();
                            SootClass sootClass = (SootClass) iterator.next();
                            String className = sootClass.getName();

                            if (!className.contains("android.support") && !className.contains("android.arch")) {
                                List<SootMethod> sootMethodList = sootClass.getMethods();

                                for (int i = 0; i < sootMethodList.size(); i++) {
                                    SootMethod sootMethod = sootMethodList.get(i);

                                    String currMethodName = sootMethod.getName();
                                    List<Type> paramMethods = sootMethod.getParameterTypes();
                                    String fullMethodDeclaration = currMethodName + "(";

                                    for (int f = 0; f < paramMethods.size(); f++) {
                                        if (f == paramMethods.size() - 1) {
                                            fullMethodDeclaration = fullMethodDeclaration.concat(String.valueOf(paramMethods.get(f)));
                                        } else {
                                            fullMethodDeclaration = fullMethodDeclaration.concat(String.valueOf(paramMethods.get(f)));
                                            fullMethodDeclaration = fullMethodDeclaration.concat(",");
                                        }
                                    }

                                    fullMethodDeclaration = fullMethodDeclaration.concat(")|");
                                    fullMethodDeclaration = fullMethodDeclaration.concat(sootClass.getName().toString());
                                    final String fullMethodName = fullMethodDeclaration;
                                    System.out.println("fullMethodName: "+fullMethodName);


                                    if (sootMethod.hasActiveBody()) {
                                        c1++;
                                        Body body1 = sootMethod.retrieveActiveBody();
                                        final PatchingChain<Unit> unit = body1.getUnits();
                                        int x = 0;
                                        for (Iterator<Unit> itr = unit.snapshotIterator(); itr.hasNext(); ) {
                                            final Unit u = itr.next();
                                            u.apply(new AbstractStmtSwitch() {
                                                @Override
                                                public void caseInvokeStmt(InvokeStmt stmt) {
                                                    InvokeExpr invokeExpr = stmt.getInvokeExpr();

                                                    if (!invokeExpr.getMethod().getName().contains("<init>")) {
                                                        String subMethodName = invokeExpr.getMethod().getName();
                                                        String subMethodClassName = invokeExpr.getMethod().getDeclaringClass().getName();
                                                        List<Type> subMethodParams = invokeExpr.getMethod().getParameterTypes();

                                                        subMethodName = subMethodName.concat("(");
                                                        for (int f = 0; f < subMethodParams.size(); f++) {
                                                            if (f == subMethodParams.size() - 1) {
                                                                subMethodName = subMethodName.concat(String.valueOf(subMethodParams.get(f)));
                                                            } else {
                                                                subMethodName = subMethodName.concat(String.valueOf(subMethodParams.get(f)));
                                                                subMethodName = subMethodName.concat(",");
                                                            }
                                                        }

                                                        subMethodName = subMethodName.concat(")");
                                                        subMethodName = subMethodName.concat("|" + subMethodClassName);
                                                        System.out.println("SubMethodNames: " + subMethodName);

                                                        if (mStartService.contains(subMethodName)) {
                                                            if (!mStartService.contains(fullMethodName)) {
                                                                mStartService.add(fullMethodName);
                                                            }
                                                        }

                                                        if (mStopService.contains(subMethodName)) {
                                                            if (!mStopService.contains(fullMethodName)) {
                                                                mStopService.add(fullMethodName);
                                                            }
                                                        }

                                                        if (mStartForeground.contains(subMethodName)) {
                                                            if (!mStartForeground.contains(fullMethodName)) {
                                                                mStartForeground.add(fullMethodName);
                                                            }
                                                        }

                                                        if (mStopForeground.contains(subMethodName)) {
                                                            if (!mStopForeground.contains(fullMethodName)) {
                                                                mStopForeground.add(fullMethodName);
                                                            }
                                                        }
                                                    }

                                                    super.caseInvokeStmt(stmt);
                                                }
                                            });
                                        }
                                    }
                                    if(!sootMethod.hasActiveBody())
                                    {
                                        c2++;
                                    }

                                }

                            }

                        }
                    }
                    count++;
                }
            }));

//                    String[] sootArgs = new String[]{
//                            "-process-multiple-dex",
//                            "-android-jars",
//                            Constants.ANDROID_JAR,
//                            "-process-dir",
//                            Constants.APK_DIR + Constants.APK_NAME
//                    };

            String[] sootArgs = new String[]{
                    "-process-multiple-dex",
                    "-android-jars",
                    Constants.ANDROID_JAR,
                    "-d",
                    "/media/dhruv2601/Carseat/Top500FreeAppDataset/jimple_output/",
                    "-process-dir",
                    apk_path
            };

            soot.Main.main(sootArgs);

            potentMethods.clear();

            potentMethods.add("Iteration "+iter+" -> ");

//            if(!(iter==3))
//            {
//                FileUtils.deleteDirectory(new File("/media/dhruv2601/Carseat/Top500FreeAppDataset/jimple_output/"));
//            }

            potentMethods.add("\n");
            for (int w = 0; w < mStartService.size(); w++) {
                potentMethods.add(mStartService.get(w));
            }
            potentMethods.add("\n");
            for (int w = 0; w < mStopService.size(); w++) {
                potentMethods.add(mStopService.get(w));
            }
            potentMethods.add("\n");
            for (int w = 0; w < mStartForeground.size(); w++) {
                potentMethods.add(mStartForeground.get(w));
            }
            potentMethods.add("\n");
            for (int w = 0; w < mStopForeground.size(); w++) {
                potentMethods.add(mStopForeground.get(w));
            }
            potentMethods.add("\n");
            potentMethods.add("Active: "+ c1 +"  !Active body: "+c2+"\n");

            String logger = String.join("\n", potentMethods);
//            clearFile(RESULTFILE);
//            printFile(RESULTFILE, logger);

                    String path = "";
                    path = RESULTFILE;
                    for(int f=0;f<line.length();f++)
                    {
                        if(line.charAt(f)=='.'&&line.charAt(f+1)=='a'&&line.charAt(f+2)=='p'&&line.charAt(f+3)=='k')
                        {
                            break;
                        }
                        else
                        {
                            path+=line.charAt(f);
                        }
                    }
                    path+=".txt";
                    clearFile(path);
                    printFile(path, logger);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
    }


    public static void clearFile(String fileName)
    {
        File file = new File(fileName);
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void printFile(String fileName, String content){
        System.out.println("Writing output file to "+fileName);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JimpleBody getJimpleBody(SootMethod sootMethod)
    {
        if(sootMethod.hasActiveBody())
        {
            final Body activeBody = sootMethod.getActiveBody();
            if(activeBody instanceof JimpleBody)
            {
                return (JimpleBody) activeBody;
            }

            JimpleBody body = jimpleBodyCache.get(sootMethod);
            if(body!=null)
            {
                return body;
            }

            jimpleBodyCache.put(sootMethod, body);
            return body;
        }
        return null;
    }
}