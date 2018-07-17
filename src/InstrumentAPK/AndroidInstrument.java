package InstrumentAPK;

import java.util.Iterator;
import java.util.Map;

import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.*;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.util.Cons;


public class AndroidInstrument {

    private static final String RESULTFILE = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/outputFile.txt";

    public static void main(String[] args) {

//        String args[] = new String[2];
//        args[0] = "-process-dir";
//        args[1] = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/MapDisplay.apk";
//        Settings.initialiseSoot();

        String apkFile = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/locationshare.apk";
        //print current apk
        //System.out.print(apkFile);
        printFile(RESULTFILE,apkFile+"\n");

        // print title
        String title = "Location API	|	Request Interval	|	Priority	|	Distance	|	Complete Location Request";
        printFile(RESULTFILE,title);

        // Array to store location request, frequency and priority
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
                                provider.add("startForeground");
                            }
                            if(invokeExpr.getMethod().getName().equals("startService"))
                            {
                                provider.add("startService");
                            }

//                            locationRequest.add(String.valueOf(invokeExpr));
//                            if(invokeExpr.getMethod().getName().equals("startService")){
//                                if(invokeExpr.getMethod().getDeclaringClass().getName().equals("com.google.android.gms.location.FusedLocationProviderApi")){
//                                    provider.add("Google Location API");
//                                } else if (invokeExpr.getMethod().getDeclaringClass().getName().equals("android.location.LocationManager")){
//                                    provider.add("Location Manager");
//                                    locationRequest.add("android.location.LocationManager");    //just added
//                                }
//                                locationRequest.add(String.valueOf(invokeExpr));
//
//                            } else if (invokeExpr.getMethod().getName().equals("requestSingleUpdate")){
//                                if(invokeExpr.getMethod().getDeclaringClass().getName().equals("android.location.LocationManager")){
//                                    provider.add("Location Manager Single Update");
//                                    locationRequest.add(String.valueOf(invokeExpr));
//                                }
//                            }
//                            else if (invokeExpr.getMethod().getName().equals("setInterval")){
//                                if(invokeExpr.getMethod().getDeclaringClass().getName().equals("com.google.android.gms.location.LocationRequest")){
//                                    locationInterval.add(String.valueOf(invokeExpr.getArg(0)));
//                                }
//                            }else if (invokeExpr.getMethod().getName().equals("setPriority")){
//                                if(invokeExpr.getMethod().getDeclaringClass().getName().equals("com.google.android.gms.location.LocationRequest")){
//                                    locationPriority.add(String.valueOf(invokeExpr.getArg(0)));
//                                }
//                            }else if (invokeExpr.getMethod().getName().equals("setSmallestDisplacement")){
//                                if(invokeExpr.getMethod().getDeclaringClass().getName().equals("com.google.android.gms.location.LocationRequest")){
//                                    locationDistance.add(String.valueOf(invokeExpr.getArg(0)));
//                                }
//                            }

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
//        soot.Main.main(args);
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
            // no need to close it.
            //bw.close();
            //System.out.println("Done");

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