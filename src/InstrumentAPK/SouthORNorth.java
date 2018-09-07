package InstrumentAPK;

import com.google.common.collect.Lists;
import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.options.Options;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static soot.SootClass.HIERARCHY;

public class SouthORNorth {

    public static int count = 0;
    public static ArrayList<List<SootClass>> listHL = new ArrayList<>();
    public static List<SootClass> fsList = new ArrayList<>();
    public static List<SootClass> asList = new ArrayList<>();
    public static PatchingChain<Unit> backupUnit;


    public static void main(String args[])
    {
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_allow_phantom_refs(true);
        Options.v().whole_program();

        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.InvalidPropertiesFormatException",HIERARCHY);

        PackManager.v().getPack("jtp").add(new Transform("jtp.MyInstrumentor", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map) {
                final PatchingChain<Unit> units = body.getUnits();
                backupUnit = units;

                int indexHL = 0;

                if(count == 0)
                {
                    Iterator iterator = Scene.v().getClasses().snapshotIterator();

                    while(iterator.hasNext())
                    {
                        SootClass sootClass = (SootClass) iterator.next();

//                        check for active body functions
//                        List<SootMethod> sootMethodList = sootClass.getMethods();

//                        for(int g=0;g<sootMethodList.size();g++)
//                        {
//                            if(sootMethodList.get(g).hasActiveBody() && !sootClass.getName().contains("android.support"))
//                            {
//                                System.out.println("active body: "+sootClass+" | "+sootMethodList.get(g).getName());
//                            }
//                        }

                        List<SootClass> tempList = new ArrayList<>();

                        if(!sootClass.getName().contains("android.support") && !sootClass.getName().contains("android.arch"))
                        {
                            tempList = getHierarchyForServiceClass(sootClass);
                        }

                        if(tempList.size()>0)
                        {
                            listHL.add(indexHL, tempList);
                            indexHL++;
                        }
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
                        "-w",
                        "-d",
                        "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/jimple_output/",
                        "-process-dir",
                        "//home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/LocationTesterDhruv4.apk"
                };

        soot.Main.main(sootArgs);

        for(int r=0;r<listHL.size();r++)
        {
            for(int h=r+1;h<listHL.size();h++)
            {
                if(listHL.get(r).equals(listHL.get(h)))
                {
                    listHL.remove(listHL.get(h));
                }
            }
        }

        System.out.println("Size: "+listHL.size());
        for(int r=0;r<listHL.size();r++)
        {
            List<SootClass> tempList = listHL.get(r);
            System.out.print("List of classes:  ");

            if(tempList.isEmpty())
            {
                System.out.println("List is empty");
            }

            for(int t=0;t<tempList.size();t++)
            {
                System.out.print(tempList.get(t)+" ");
            }
            System.out.println("\n");
        }


        for(int q=0;q<listHL.size();q++)
        {
            GOD(listHL.get(q));
        }

        System.out.println("List of foreground service classes is as follows: ");
        for(int q=0;q<fsList.size();q++)
        {
            System.out.println(fsList.get(q));
        }
    }

    public static void GOD(List<SootClass> hierarchyList)
    {
        for(int i=hierarchyList.size()-1; i>=0; i--)
        {
//            soot.G.reset();
//            sootInit();
//
//

            SootClass sootClass = hierarchyList.get(i);

            if(asList.contains(sootClass))
            {
                if(fsList.contains(sootClass))
                {
                    for(int j=i-1;j>=0;j--)
                    {
                        fsList.add(hierarchyList.get(j));
                    }
                }
            }

            else
            {
                asList.add(sootClass);
                List<SootMethod> sootMethodList = sootClass.getMethods();

                for(int j=0;j<sootMethodList.size();j++)
                {
                    SootMethod sootMethod = sootMethodList.get(j);
                    String currentMethodName = sootMethod.getName();
                    if(currentMethodName.equals("startForeground"))
                    {
                        for(int g=i;g<hierarchyList.size()-1;g++)
                        {
                            fsList.add(hierarchyList.get(g));
                        }
                    }

                    else
                    {
                        if(sootMethod.hasActiveBody())
                        {
                            System.out.println("active: "+sootClass.getName()+" | "+sootMethod.getName());
                            Body body = sootMethod.retrieveActiveBody();
                            final PatchingChain<Unit> unit = body.getUnits();
                            for(Iterator<Unit> iterator = unit.snapshotIterator(); iterator.hasNext();)
                            {
                                final Unit u = iterator.next();
                                int finalI = i;
                                u.apply(new AbstractStmtSwitch() {
                                    @Override
                                    public void caseInvokeStmt(InvokeStmt stmt) {

                                        InvokeExpr invokeExpr = stmt.getInvokeExpr();

                                        if(!invokeExpr.getMethod().getName().contains("<init>"))
                                        {
                                            if(invokeExpr.getMethod().getName().contains("startForeground"))
                                            {
                                                List<Type> paramList = invokeExpr.getMethod().getParameterTypes();
                                                if(paramList.get(0).equals("int") && paramList.get(1).equals("android.app.Notification"))
                                                {
                                                    for(int g = finalI; g>=0; g--)
                                                    {
                                                        fsList.add(hierarchyList.get(g));
                                                    }
                                                }
                                            }
                                        }

                                        super.caseInvokeStmt(stmt);
                                    }
                                });
                            }
                        }

                        int count = 0;

                        if(!sootMethod.hasActiveBody())
                        {
                            SootClass ownerSootClass = sootMethod.getDeclaringClass();
                            for(int r=0;r<listHL.size();r++)
                            {
                                if(listHL.get(r).get(0).equals(ownerSootClass))
                                {
                                    Iterator iterator = Scene.v().getClasses().snapshotIterator();
                                    while(iterator.hasNext())
                                    {
                                        SootClass jumpSootClass = (SootClass) iterator.next();
                                        if(jumpSootClass.equals(ownerSootClass))
                                        {
                                            List<SootMethod> ownerSootMethodList = jumpSootClass.getMethods();
                                            for(int w=0;w<ownerSootMethodList.size();w++)
                                            {
                                                if(ownerSootMethodList.get(w).equals(sootMethod))
                                                {
                                                    sootMethod = ownerSootMethodList.get(w);
                                                    if(sootMethod.hasActiveBody())
                                                    {
                                                        Body body = sootMethod.retrieveActiveBody();
                                                        final PatchingChain<Unit> units = body.getUnits();
                                                        for(Iterator<Unit> its = units.snapshotIterator(); its.hasNext();)
                                                        {
                                                            final Unit u = its.next();
                                                            int finalI1 = i;
                                                            u.apply(new AbstractStmtSwitch() {
                                                                @Override
                                                                public void caseInvokeStmt(InvokeStmt stmt) {
                                                                    InvokeExpr invokeExpr = stmt.getInvokeExpr();

                                                                    if(!invokeExpr.getMethod().getName().contains("<init>"))
                                                                    {
                                                                        if(invokeExpr.getMethod().getName().contains("startForeground"))
                                                                        {
                                                                            List<Type> paramsList = invokeExpr.getMethod().getParameterTypes();
                                                                            if(paramsList.get(0).equals("int") && paramsList.get(1).equals("android.app.Notification"))
                                                                            {
                                                                                for(int h = finalI1; h>=0; h--)
                                                                                {
                                                                                    fsList.add(hierarchyList.get(h));
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    super.caseInvokeStmt(stmt);
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static List<SootClass> getHierarchyForServiceClass(SootClass sootClass)
    {
        List<SootClass> storeHierarchy = new ArrayList<>(0);
        int flag = 0;

        storeHierarchy.add(sootClass);

        while(gotParent(sootClass))
        {
            sootClass = sootClass.getSuperclass();
            if(!sootClass.getName().equals("android.app.IntentService"))
            {
                storeHierarchy.add(sootClass);
            }
            String tempName = sootClass.getName();
            if(tempName.equals("android.app.Service"))
            {
                int size = storeHierarchy.size();
                storeHierarchy.remove(size-1);
                flag = 1;
                break;
            }
        }
        if(flag == 0)
        {
            storeHierarchy.clear();
        }
        return storeHierarchy;
    }

    public static boolean gotParent(SootClass sootClass)
    {
        return sootClass.hasSuperclass();
    }

    public static void sootInit()
    {
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_allow_phantom_refs(true);
        Options.v().whole_program();

        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.util.InvalidPropertiesFormatException",HIERARCHY);
    }
}