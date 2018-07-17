package InstrumentAPK;

import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.options.Options;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class AndroidInstrument {

    private static final String RESULTFILE = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/outputFile.txt";

    public static void main(String[] args) {


        String apkFile = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/locationshare.apk";

        printFile(RESULTFILE,apkFile+"\n");

        String title = "Location API	|	Request Interval	|	Priority	|	Distance	|	Complete Location Request";
        printFile(RESULTFILE,title);

        final List<String> locationRequest = new ArrayList<String>();
        final List<String> locationInterval = new ArrayList<String>();
        final List<String> locationPriority = new ArrayList<String>();
        final List<String> locationDistance = new ArrayList<String>();
        final List<String> provider = new ArrayList<String>();


        Options.v().set_src_prec(Options.src_prec_apk);

        // output as APK, too//-f J
        Options.v().set_output_format(Options.output_format_none);
        // Options.v().set_output_format(Options.output_format_jimple);

        // Borrowed from CallTracer.
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);


        // resolve the PrintStream and System soot-classes
        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

            @Override
            protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                final PatchingChain<Unit> units = b.getUnits();
//                System.out.println("internalTransform11111 is here!!");

                //important to use snapshotIterator here
                for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
                    final Unit u = iter.next();
                    u.apply(new AbstractStmtSwitch() {

                        public void caseInvokeStmt(InvokeStmt stmt) {

                            InvokeExpr invokeExpr = stmt.getInvokeExpr();

                            if(invokeExpr.getMethod().getName().equals("startForegroundService"))
                            {
                                provider.add("startForegroundService");
                            }

                            if(invokeExpr.getMethod().getName().equals("startForeground"))
                            {
                                provider.add(Scene.v().getActiveHierarchy().toString()+"    ");
                                provider.add("startForeground");
                            }

                            if(invokeExpr.getMethod().getName().equals("startService"))
                            {
                                provider.add(Scene.v().getActiveHierarchy().toString()+"    ");
                                provider.add("startService");
                            }
                        }

                    });
                }
            }


        }));

        String[] sootArgs = new String[]{
                "-android-jars",
                Constants.ANDROID_JAR,
                "-process-dir",
                Constants.APK_DIR+Constants.APK_NAME
//                "-d",
//                Constants.OUTPUT_DIR
//                "-force-android-jar",
//                Constants.ANDROID_JAR + "android-25/android.jar"
        };
        soot.Main.main(sootArgs);

        String locationProvider = String.join(" ; ", provider);
        String locationRequestString = String.join(" ; ", locationRequest);
        String locationIntervalString = String.join(" ; ", locationInterval);
        String locationPriorityString = String.join(" ; ", locationPriority);
        String locationDistanceString = String.join(" ; ", locationDistance);
        String locationResult = "\n" + locationProvider + "	|	" + locationIntervalString + "	|	" + locationPriorityString + "	|	" + locationDistanceString + "	|	" + locationRequestString +"\n";
        printFile(RESULTFILE, locationResult + "\n\n");
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