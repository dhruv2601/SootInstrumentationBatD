package InstrumentAPK;

import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.options.Options;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InstrumentCurrentTest {

    private static final String RESULTFILE = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/outputFile.txt";
    private static int numSS=0;
    private static int numSFS=0;
    private static int numSF=0;

    public static void main(String[] args) {

        final List<String> locationRequest = new ArrayList<String>();
        final List<String> locationInterval = new ArrayList<String>();
        final List<String> locationPriority = new ArrayList<String>();
        final List<String> locationDistance = new ArrayList<String>();
        final List<String> provider = new ArrayList<String>();

        Options.v().set_src_prec(Options.src_prec_apk);

        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);

        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

            @Override
            protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                final PatchingChain<Unit> units = b.getUnits();

                //important to use snapshotIterator here
                for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
                    final Unit u = iter.next();
                    u.apply(new AbstractStmtSwitch() {

                        public void caseInvokeStmt(InvokeStmt stmt) {

                            InvokeExpr invokeExpr = stmt.getInvokeExpr();

//                            provider.add(invokeExpr.toString()+"\n");

                            if(invokeExpr.getMethod().getName().equals("startForegroundService"))
                            {

                                List<soot.Type> paramList = invokeExpr.getMethod().getParameterTypes();

                                for(int j=0; j<paramList.size(); j++)
                                {
                                    if(paramList.get(j).toString().equals("android.content.Intent"))
                                    {
                                        numSFS++;
                                        provider.add("startForegroundService");
                                        break;
                                    }
                                }

                            }

                            if(invokeExpr.getMethod().getName().equals("startForeground"))
                            {
                                List<soot.Type> paramList = invokeExpr.getMethod().getParameterTypes();

                                for(int j=0; j<paramList.size(); j++)
                                {
                                    if(paramList.get(j).toString().equals("android.app.Notification"))
                                    {
                                        numSF++;
                                        provider.add("startForeground");
                                        break;
                                    }
                                }
                            }

                            if(invokeExpr.getMethod().getName().equals("startService"))
                            {
                                List<soot.Type> paramList = invokeExpr.getMethod().getParameterTypes();

                                provider.add("className:: "+invokeExpr.getMethod().getDeclaringClass().getName() +
                                        " " + invokeExpr.getMethod().isJavaLibraryMethod());
                                for(int j=0; j<paramList.size(); j++)
                                {
                                    if(paramList.get(j).toString().equals("android.content.Intent")
                                            &&(invokeExpr.getMethod().getDeclaringClass().getName()
                                            .equals("android.content.Context")))
                                    {
                                        numSS++;
                                        provider.add("startService");
                                        break;
                                    }
                                }

                            }
                        }
                    });
                }
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
        String locationRequestString = String.join(" ; ", locationRequest);
        String locationIntervalString = String.join(" ; ", locationInterval);
        String locationPriorityString = String.join(" ; ", locationPriority);
        String locationDistanceString = String.join(" ; ", locationDistance);
        String locationResult = "\n" + locationProvider + "	|	" + locationIntervalString + "	|	" + locationPriorityString + "	|	" + locationDistanceString + "	|	" + locationRequestString +"\n";
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