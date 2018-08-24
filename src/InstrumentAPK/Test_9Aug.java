package InstrumentAPK;

import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.toolkits.callgraph.ContextSensitiveCallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Test_9Aug {

    private static final String RESULTFILE = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/test_9Aug.txt";
    private static final String APK_NAME = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/com.whatsapp.apk";

    public static void main(String[] args)
    {
        final List<String> finalList = new ArrayList<String>();
        final List<String> provider = new ArrayList<String>();

        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);
        Options.v().set_android_jars(Constants.ANDROID_JAR);

        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);

        final int[] count = {0};
        final int[] counter = {0};
        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map) {
                final PatchingChain<Unit> units = body.getUnits();

                if(count[0] == 0)
                {
                    Iterator iterator = Scene.v().getClasses().snapshotIterator();
                    while(iterator.hasNext())
                    {
                        SootClass sootClass = (SootClass) iterator.next();
                        String className = sootClass.getName();

                        if(!className.contains("android.support") && !className.contains("android.arch"))
                        {
                            counter[0]++;
                            List<SootMethod> sootMethodList = sootClass.getMethods();
                            for(int d=0;d<sootMethodList.size();d++)
                            {
                                SootMethod sootMethod = sootMethodList.get(d);
                                if(sootMethod.getName().contains("startService"))
                                {
                                    provider.add("startService"+" | "+className);
                                    provider.add("\n");
                                }
                                if(sootMethod.getName().contains("stopService"))
                                {
                                    provider.add("stopService"+" | "+className);
                                    provider.add("\n");
                                }
                                if(sootMethod.getName().contains("startForeground"))
                                {
                                    provider.add("startForeground"+" | "+className);
                                    provider.add("\n");
                                }
                                if(sootMethod.getName().contains("stopForeground"))
                                {
                                    provider.add("stopForeground"+" | "+className);
                                    provider.add("\n");
                                }
                            }
                            System.out.println("No. "+counter[0]+" className "+className);
                        }
                    }
                }

                count[0]++;

                for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();)
                {
                    final Unit u = iter.next();
                    u.apply(new AbstractStmtSwitch() {
                        @Override
                        public void caseInvokeStmt(InvokeStmt stmt) {

                            InvokeExpr invokeExpr = stmt.getInvokeExpr();

                            if(invokeExpr.getMethod().getName().contains("startService"))
                            {
                                provider.add("startService"+" | "+invokeExpr.getMethod().getDeclaringClass().getName());
                                provider.add("\n");
                            }
                            if(invokeExpr.getMethod().getName().contains("stopService"))
                            {
                                provider.add("stopService"+" | "+invokeExpr.getMethod().getDeclaringClass().getName());
                                provider.add("\n");
                            }
                            if(invokeExpr.getMethod().getName().contains("startForeground"))
                            {
                                provider.add("startForeground"+" | "+invokeExpr.getMethod().getDeclaringClass().getName());
                                provider.add("\n");
                            }
                            if(invokeExpr.getMethod().getName().contains("stopForeground"))
                            {
                                provider.add("stopForeground"+" | "+invokeExpr.getMethod().getDeclaringClass().getName());
                                provider.add("\n");
                            }

                            super.caseInvokeStmt(stmt);
                        }
                    });
                }
            }
        }));

        String sootArgs[] = new String[]
                {
                        "-android-jars",
                        Constants.ANDROID_JAR,
                        "-process-dir",
                        Constants.APK_DIR+Constants.APK_NAME
                };
        soot.Main.main(sootArgs);

        String finalProviderDetails = provider+"\n";
        printFile(RESULTFILE, Constants.APK_DIR+Constants.APK_NAME+"\n");
        printFile(RESULTFILE, finalProviderDetails);
    }

    private static void printFile(String fileName, String content){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}