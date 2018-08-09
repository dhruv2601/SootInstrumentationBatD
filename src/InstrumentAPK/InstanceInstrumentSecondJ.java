package InstrumentAPK;

import soot.*;
import soot.baf.BafBody;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.options.Options;
import sun.management.counter.Units;

import javax.swing.text.html.Option;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class InstanceInstrumentSecondJ {
    public static final String RESULTFILE = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/outputFile.txt";
    public static int count = 0;
    public static final Map<SootMethod, JimpleBody> jimpleBodyCache = new HashMap<SootMethod, JimpleBody>();
    public static List<String> mStartService = new ArrayList<>();
    public static List<String> mStopService = new ArrayList<>();
    public static List<String> mStartForeground = new ArrayList<>();
    public static List<String> mStopForeground = new ArrayList<>();
    static int sizeStartService = mStartService.size();
    static int sizeStopService = mStopService.size();
    static int sizeStartForeground = mStartForeground.size();
    static int sizeStopForeground = mStopForeground.size();

    public static void main(String[] args)
    {
        final List<String> logging = new ArrayList<>();
        final List<String> potentMethods = new ArrayList<>();

        mStartService.add("startService(android.content.Intent)");
        mStopService.add("stopService(android.content.Intent)");
        mStartForeground.add("startForeground(int,android.app.Notification)");
        mStopForeground.add("stopForeground(int)");
        mStopForeground.add("stopForeground(boolean)");

        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_src_prec(Options.src_prec_apk);

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);

        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.land.System", SootClass.SIGNATURES);

        int jtpAppend = 0;
//        while((sizeStartService <= mStartService.size()) && (sizeStopService <= mStopService.size())
//                && (sizeStartForeground <= mStartForeground.size()) && (sizeStopForeground <= mStopForeground.size()))
        {
            soot.G.reset();

            Options.v().set_output_format(Options.output_format_jimple);
            Options.v().set_src_prec(Options.src_prec_apk);

            Options.v().set_allow_phantom_refs(true);
            Options.v().set_whole_program(true);

            Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
            Scene.v().addBasicClass("java.land.System", SootClass.SIGNATURES);

            count = 0;
//            String phaseName = "jtp.MyInstrumentor".concat(String.valueOf((jtpAppend)));
//            jtpAppend++;

            PackManager.v().getPack("jtp").add(new Transform("jtp.MyInstrumentor", new BodyTransformer() {
                @Override
                protected void internalTransform(Body body, String s, Map<String, String> map) {
                    final PatchingChain<Unit> units = body.getUnits();

                    if(count==0)
                    {
                        Iterator iterator = Scene.v().getClasses().snapshotIterator();
//                        Iterator iterator = Scene.v().getApplicationClasses().snapshotIterator();
//                        potentMethods.add(String.valueOf("Application classes total "+Scene.v().getApplicationClasses().size()));
//                        potentMethods.add(String.valueOf("Total Classes: "+Scene.v().getClasses().size()));
                        while(iterator.hasNext())
                        {
                            logging.clear();
                            SootClass sootClass = (SootClass) iterator.next();
                            String className = sootClass.getName();

                            if(!className.contains("android.support") && !className.contains("android.arch"))
                            {
                                List<SootMethod> sootMethodList = sootClass.getMethods();

                                for(int i=0;i<sootMethodList.size();i++)
                                {
                                    SootMethod sootMethod = sootMethodList.get(i);

                                    JimpleBody jimpleBody = getJimpleBody(sootMethod);
                                    String currMethodName = sootMethod.getName();
                                    List<Type> paramMethods = sootMethod.getParameterTypes();
                                    String fullMethodDeclaration = currMethodName+"(";

                                    for(int f=0;f<paramMethods.size();f++)
                                    {
                                        if(f==paramMethods.size()-1)
                                        {
                                            fullMethodDeclaration = fullMethodDeclaration.concat(String.valueOf(paramMethods.get(f)));
                                        }
                                        else
                                        {
                                            fullMethodDeclaration = fullMethodDeclaration.concat(String.valueOf(paramMethods.get(f)));
                                            fullMethodDeclaration = fullMethodDeclaration.concat(",");
                                        }
                                    }

                                    fullMethodDeclaration = fullMethodDeclaration.concat(")");
                                    fullMethodDeclaration = fullMethodDeclaration.concat(sootClass.getName().toString());

                                    if (jimpleBody != null) {
                                        if(!logging.contains(jimpleBody.toString()))
                                        {
                                            String methodCode = jimpleBody.toString();
                                            for(int q=0; q < mStartService.size(); q++)
                                            {
                                                if(methodCode.contains(mStartService.get(q)))
                                                {
                                                    if(!mStartService.contains(fullMethodDeclaration))
                                                    {
                                                        mStartService.add(fullMethodDeclaration);
                                                    }
                                                }
                                            }

                                            for(int q=0; q<mStartForeground.size(); q++)
                                            {
                                                if(methodCode.contains(mStartForeground.get(q)))
                                                {
                                                    if(!mStartForeground.contains(fullMethodDeclaration))
                                                    {
                                                        mStartForeground.add(fullMethodDeclaration);
//                                                        mStartForeground.add(" + ");
//                                                        mStartForeground.add(sootClass.getName().toString());
                                                    }
                                                }
                                            }

                                            for(int q=0;q<mStopService.size(); q++)
                                            {
                                                if(methodCode.contains(mStopService.get(q)))
                                                {
                                                    if(!mStopService.contains(fullMethodDeclaration))
                                                    {
                                                        mStopService.add(fullMethodDeclaration);
                                                    }
                                                }
                                            }

                                            for(int q=0; q<mStopForeground.size() ;q++)
                                            {
                                                if(methodCode.contains(mStopForeground.get(q)))
                                                {
                                                    if(!mStopForeground.contains(fullMethodDeclaration))
                                                    {
                                                        mStopForeground.add(fullMethodDeclaration);
                                                    }
                                                }
                                            }

                                            logging.add(jimpleBody.toString());
                                        }
                                    }

                                }

                            }

                        }
                    }
                    count++;
                }
            }));

            String[] sootArgs = new String[]{
                    "-process-multiple-dex",
                    "-android-jars",
                    Constants.ANDROID_JAR,
                    "-process-dir",
                    Constants.APK_DIR+Constants.APK_NAME
            };
            soot.Main.main(sootArgs);
        }
        potentMethods.add("\n");
        for(int w = 0;w<mStartService.size();w++)
        {
            potentMethods.add(mStartService.get(w)+" ");
        }
        potentMethods.add("\n");
        for(int w = 0;w<mStopService.size();w++)
        {
            potentMethods.add(mStopService.get(w)+" ");
        }
        potentMethods.add("\n");
        for(int w = 0;w<mStartForeground.size();w++)
        {
            potentMethods.add(mStartForeground.get(w)+" ");
        }
        potentMethods.add("\n");
        for(int w = 0;w<mStopForeground.size();w++)
        {
            potentMethods.add(mStopForeground.get(w)+" ");
        }
        potentMethods.add("\n");

        String logger = String.join("\n", potentMethods);
        printFile(RESULTFILE, logger);

    }

    public static void printFile(String fileName, String content){
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