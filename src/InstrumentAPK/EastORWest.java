package InstrumentAPK;

import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.options.Options;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EastORWest {

    public static int count = 0;

    public static void main(String args[])
    {
        List<String> serviceClassList = new ArrayList<>(0);
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

                if(count == 0) {

                    Iterator iterator = Scene.v().getClasses().snapshotIterator();

                    List<String> classNameList = new ArrayList<>();

                    while (iterator.hasNext()) {
                        SootClass sootClass = (SootClass) iterator.next();

                        if(!sootClass.getName().contains("android.support"))
                        {
                            List<SootClass> hierarchySootClass = doesClassExtendsService(sootClass);
                            if(hierarchySootClass.size()==0)
                            {
                                System.out.println("The class "+sootClass.getName()+" does not extend service");
                            }
                            else
                            {
                                System.out.println("The hierarchy for the class " +sootClass.getName()+"goes as follows: ");
                                for(int t=0;t<hierarchySootClass.size();t++)
                                {
                                    System.out.println(hierarchySootClass.get(t).getName());
                                }
                            }
                        }


//                        int flag = 0;
//                        String className = sootClass.getName();
//
//                        serviceClassList.add(className);
//
//                        while (gotParent(sootClass)) {
//                            sootClass = sootClass.getSuperclass();
//                            String tempClassName = sootClass.getName();
//                            classNameList.add(tempClassName);
//                            if (tempClassName.equals("android.app.Service")) {
//                                break;
//                            }
//                        }
//
//                        if(classNameList.get(classNameList.size()-1).equals("android.app.Service"))
//                        {
//                            for(int f=0;f<classNameList.size();f++)
//                            {
//                                if(serviceClassList.contains(classNameList.get(f)))
//                                {
//                                    // has already processed the class, now find instances.
//                                }
//                                else
//                                {
//                                    // load the class from scene and do the analysis to get the instances.
//                                }
//                            }
//                        }
//
//                        String topSuperClassName = sootClass.getName();
//
//                        if (topSuperClassName.equals("android.app.Service") && !className.contains("android.support")) {
//                            for(int f=0;f<classNameList.size();f++)
//                            {
//                                System.out.println("Class: " + className + " Parent: " + classNameList.get(f));
//                            }
//                        }
//                        classNameList.clear();
                    }
                    count++;
                }
            }
        }));

        String[] sootArgs = new String[]
                {
                        "-process-multiple-dex",
                        "-android-jars",
                        Constants.ANDROID_JAR,
                        "-d",
                        "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/jimple_output/",
                        "-process-dir",
                        "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/LocationTesterDhruv4.apk"
                };
        soot.Main.main(sootArgs);
    }

    // initialize soot after calling soot.G.reset()
    public static void initSoot()
    {
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_src_prec(Options.src_prec_apk);

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);

        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
        Scene.v().loadNecessaryClasses();
    }

    public void analyzeSootClass(SootClass sootClass)
    {
        List<SootClass> hierarchySootClassList = new ArrayList<SootClass>(0);

        // check if it extends Service
        hierarchySootClassList = doesClassExtendsService(sootClass);
        if(hierarchySootClassList.size()==0)
        {
            //does not extend soot, no good to us, return 0 or something (TBD)
        }
        if(hierarchySootClassList.size()!=0)
        {
            // extends service and we have the list of sootClasses to analyze here,
            // but the point is that we need to analyze each method with us and see if it contains in any of it's heirarchy any calls to startForeground or startForegroundService functions, if it does then it is to be added in the sfMethod list else move on to the next function in the class list.
            for(int g=0;g<hierarchySootClassList.size();g++)
            {
                SootClass hSootClass = hierarchySootClassList.get(g);
                List<SootMethod> sootMethodList = new ArrayList<>();
                for(int h=0;h<sootMethodList.size();h++)
                {
                    SootMethod sootMethod = sootMethodList.get(h);
                    if(sootMethod.hasActiveBody())
                    {
                        //has active body, find if it contains SF and add to list
                        Body body = sootMethod.getActiveBody();
                        final PatchingChain<Unit> unit = body.getUnits();
                        for(Iterator<Unit> iterator = unit.snapshotIterator(); iterator.hasNext();)
                        {
                            final Unit u = iterator.next();
                            u.apply(new AbstractStmtSwitch() {
                                @Override
                                public void caseInvokeStmt(InvokeStmt stmt) {
                                    super.caseInvokeStmt(stmt);
                                    InvokeExpr invokeExpr = stmt.getInvokeExpr();

                                    if(invokeExpr.getMethod().getName().contains("startForeground"))
                                    {
                                        // startForeground() is found, deal with it.
                                    }
                                }
                            });
                        }
                    }
                    else if(!sootMethod.hasActiveBody())
                    {
                        SootClass classOfMethod = sootMethod.getDeclaringClass();

                        // TODO: test if later we will need to counter for phantom class.

                        analyzeSootClass(classOfMethod);
                    }
                }
            }

        }

    }

    public static List<SootClass> doesClassExtendsService(SootClass sootClass)
    {
        List<SootClass> sootClassList = new ArrayList<>(0);
        int jhanda = 0;

        while(gotParent(sootClass))
        {
            sootClass = sootClass.getSuperclass();
            String tempClassName = sootClass.getName();
            sootClassList.add(sootClass);
            if(tempClassName.equals("android.app.Service"))
            {
                jhanda = 1;
                break;
            }
        }

        if(jhanda==0)
        {
            sootClassList.clear();
        }

        return sootClassList;
    }

    public static boolean gotParent(SootClass sootClass)
    {
        return sootClass.hasSuperclass();
    }

}