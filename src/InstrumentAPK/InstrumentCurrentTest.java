package InstrumentAPK;

import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.toolkits.callgraph.Units;
import soot.options.Options;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class InstrumentCurrentTest {

    private static final String RESULTFILE = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/outputFile.txt";
    private static int numSS=0;
    private static int numSFS=0;
    private static int numSF=0;
    private static int count = 0;

    public static void main(String[] args) {

        final List<String> locationRequest = new ArrayList<String>();
        final List<String> locationInterval = new ArrayList<String>();
        final List<String> locationPriority = new ArrayList<String>();
        final List<String> locationDistance = new ArrayList<String>();
        final List<String> provider = new ArrayList<String>();

        Options.v().set_src_prec(Options.src_prec_apk);

        Options.v().set_output_format(Options.output_format_jimple);  // change it to jimple
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);

        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

            @Override
            protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                final PatchingChain<Unit> units = b.getUnits();

                if(count==0) {
                    Iterator it1 = Scene.v().getApplicationClasses().snapshotIterator();
                    while (it1.hasNext()) {
                        SootClass sc = (SootClass) it1.next();

                        if(sc.getJavaStyleName().equals("CallingServiceAct")) {
                            List<SootMethod> sootMethods = sc.getMethods();
                            for(int i=0;i<sootMethods.size();i++)
                            {
                                SootMethod sootMethod = sootMethods.get(i);
                                if(sootMethod.retrieveActiveBody()!=null)
                                {
                                    Body body = sootMethod.retrieveActiveBody();
                                    final PatchingChain<Unit> unit = body.getUnits();
                                    int x=0;
                                    for(Iterator<Unit> iterator = unit.snapshotIterator(); iterator.hasNext();)
                                    {
                                        final Unit u = iterator.next();
                                        u.apply(new AbstractStmtSwitch() {
                                            @Override
                                            public void caseInvokeStmt(InvokeStmt stmt) {
                                                InvokeExpr invokeExpr = stmt.getInvokeExpr();

                                                if(!invokeExpr.getMethod().getName().contains("<init>"))
                                                {
                                                    provider.add(invokeExpr.getMethod().getName()+" "+invokeExpr.getMethod().getDeclaringClass().getName()+"\n");
                                                }

                                                super.caseInvokeStmt(stmt);
                                            }
                                        });
                                    }
                                }

//                                provider.add(sootMethod.getName()+" "+sootMethod.getDeclaringClass().getName()+"\n");
                            }
                        }
                    }
                }
                count++;
//            }

//                -----

//                if(count==0)
//                {
//                    Iterator it1 = Scene.v().getApplicationClasses().snapshotIterator();
//                    while(it1.hasNext())
//                    {
//                        SootClass sc = (SootClass) it1.next();
//
//                        if(sc.hasSuperclass())
//                        {
//                            if(sc.getSuperclass().getName().equals("android.app.Service") ||
//                                    sc.getSuperclass().getName().equals("android.app.IntentService"))
//                            {
//                                if(!provider.contains(sc.getSuperclass().getName() + " "+sc.getName()))
//                                {
//                                    provider.add(("Lvl1  "+ sc.getSuperclass().getName() + " "+sc.getName() + "\n"));
//                                }
//                            }
//                        }
//                    }
//
//                    it1 = Scene.v().getApplicationClasses().snapshotIterator();
//                    while(it1.hasNext())
//                    {
//                        SootClass sc = (SootClass) it1.next();
//
//                        if(sc.getSuperclass().hasSuperclass()) {
//                            if (sc.getSuperclass().getSuperclass().getName().equals("android.app.Service") ||
//                                    sc.getSuperclass().getSuperclass().getName().equals("android.app.IntentService")) {
//                                if (!provider.contains(sc.getSuperclass().getSuperclass().getName() + "  " + sc.getName())) {
//                                    provider.add("Lvl2   "+sc.getSuperclass().getSuperclass().getName() + "  " + sc.getName()+"\n");
//                                }
//                            }
//                        }
//                    }
//                }
//
//                count++;

                // isse upar vala uncomment
//                ------


//                System.out.println("Size:: "+Scene.v().getApplicationClasses().size());

//                    Iterator it1 = Scene.v().getClasses().snapshotIterator();
//
////                    System.out.println("size: "+it1);
//
//                    while(it1.hasNext())
//                    {
//                        SootClass sc = (SootClass) it1.next();
//
////                        ---->>> for 2 level hierarchy
////
////                        if(sc.getSuperclass().hasSuperclass()) {
////                            if (sc.getSuperclass().getSuperclass().getName().equals("android.app.Service") ||
////                                    sc.getSuperclass().getSuperclass().getName().equals("android.app.IntentService")) {
////                                if (!provider.contains(sc.getSuperclass().getSuperclass().getName() + "  " + sc.getName())) {
////                                    provider.add("Lvl2   "+sc.getSuperclass().getSuperclass().getName() + "  " + sc.getName()+"\n");
////                                }
////                            }
////                        }
//
//
//    //                    ----->>> for single level hierarchy
//
//                        if(sc.hasSuperclass()) {
//                            if (sc.getSuperclass().getName().equals("android.app.Service") ||
//                                    sc.getSuperclass().getName().equals("android.app.IntentService")) {
//                                if (!provider.contains(sc.getSuperclass().getName() + "  " + sc.getName())) {
//                                    provider.add("Lvl1   " + sc.getSuperclass().getName() + "  " + sc.getName() + "\n");
//                                }
//                            }
//                        }
//    //                        provider.add(sc.getSuperclass().getName()+" is a library class");
//                    }


//                ----

                                //important to use snapshotIterator here
//                                for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
//                                    final Unit u = iter.next();
//
//                                    u.apply(new AbstractStmtSwitch() {
//
//                                        public void caseInvokeStmt(InvokeStmt stmt) {
//
//                                            InvokeExpr invokeExpr = stmt.getInvokeExpr();
//
//                                            if(invokeExpr.getMethod().getName().equals("startForegroundService"))
//                                            {
//                                                provider.add("SubSign: "+invokeExpr.getMethod().getSubSignature()+"\n");
//
//                                                List<soot.Type> paramList = invokeExpr.getMethod().getParameterTypes();
//
//                                                for(int j=0; j<paramList.size(); j++)
//                                                {
//                                                    if(paramList.get(j).toString().equals("android.content.Intent"))
//                                                    {
//                                                        numSFS++;
//                                                        provider.add("startForegroundService");
//                                                        break;
//                                                    }
//                                                }
//                                            }
//
//                                            if(invokeExpr.getMethod().getName().equals("startForeground"))
//                                            {
//                                                List<soot.Type> paramList = invokeExpr.getMethod().getParameterTypes();
//
//                                                for(int j=0; j<paramList.size(); j++)
//                                                {
//                                                    if(paramList.get(j).toString().equals("android.app.Notification"))
//                                                    {
//                                                        numSF++;
//                                                        provider.add("startForeground");
//                                                        break;
//                                                    }
//                                                }
//                                            }
//
//                                            if(invokeExpr.getMethod().getName().equals("startService"))
//                                            {
//                                                List<soot.Type> paramList = invokeExpr.getMethod().getParameterTypes();
//
//                //                                provider.add("className:: "+invokeExpr.getMethod().getDeclaringClass().getName() +
//                //                                        " " + invokeExpr.getMethod().isJavaLibraryMethod());
//
//                                                for(int j=0; j<paramList.size(); j++)
//                                                {
//                                                    if(paramList.get(j).toString().equals("android.content.Intent")
//                                                            &&(invokeExpr.getMethod().getDeclaringClass().getName()
//                                                            .equals("android.content.Context")))
//                                                    {
//                                                        numSS++;
//                                                        provider.add("startService");
//                                                        break;
//                                                    }
//                                                }
//
//                                            }
//                                        }
//                                    });
//                                }

//                                ----

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

        //        soot.Main.main(args);

        String locationProvider = String.join(" ; ", provider);
        String locationResult = "\n" + locationProvider +"\n";
        printFile(RESULTFILE, locationResult +"\n"+String.valueOf(numSF)+" "+String.valueOf(numSFS)+" "+String.valueOf(numSS)+"\n\n");
        System.out.println(String.valueOf(numSF)+" "+String.valueOf(numSFS)+" "+String.valueOf(numSS)+"\n");

        int arr[] = new int[3];
        arr[0] = numSS;
        arr[1] = numSF;
        arr[2] = numSFS;
    }

    public static void printFile(String fileName, String content){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Local addTmpRef(Body body)
    {
        Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
        body.getLocals().add(tmpRef);
        return tmpRef;
    }

    private static Local addTmpString(Body body)
    {
        Local tmpString = Jimple.v().newLocal("tmpString", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }
}