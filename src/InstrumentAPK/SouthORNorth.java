package InstrumentAPK;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import soot.*;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.options.Options;

import java.util.*;

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
                        "//home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/com.google.android.apps.youtube.music.apk"
                };

        soot.Main.main(sootArgs);

        System.out.println("\n");
//        ImmutableSet.copyOf(listHL);

        Set<List<SootClass>> hs = new HashSet<>();
        hs.addAll(listHL);
        listHL.clear();
        listHL.addAll(hs);

        System.out.println("Size: "+listHL.size());
        for(int r=0;r<listHL.size();r++)
        {
            List<SootClass> tempList = new ArrayList<>();
            tempList = listHL.get(r);
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

        System.out.println("\n");
//        ImmutableSet.copyOf(fsList);
        Set<SootClass> hs1 = new HashSet<>();
        hs1.addAll(fsList);
        fsList.clear();
        fsList.addAll(hs1);

        System.out.println("List of foreground service classes is as follows: ");
        for(int q=0;q<fsList.size();q++)
        {
            System.out.println(fsList.get(q));
        }

        System.out.println("\n");

//        ImmutableSet.copyOf(asList);
        Set<SootClass> hs2 = new HashSet<>();
        hs2.addAll(asList);
        asList.clear();
        asList.addAll(hs2);

        System.out.println("List of analyzed service classes is as follows: ");
        for(int q=0;q<asList.size();q++)
        {
            System.out.println(asList.get(q));
        }
    }

    public static int i;
    public static int count1 = 0;
    public static void GOD(List<SootClass> hierarchyList)
    {
        count1 = 0;
        for(i=hierarchyList.size()-1; i>=0; i--)
        {
            soot.G.reset();
            sootInit();

            count1 = 0;

            int finalI2 = i;
            PackManager.v().getPack("jtp").add(new Transform("jtp.MyInstrumentor", new BodyTransformer() {
                @Override
                protected void internalTransform(Body body, String s, Map<String, String> map) {
                    final PatchingChain<Unit> units = body.getUnits();

                    if(count1==0)
                    {
                        Iterator iterator = Scene.v().getClasses().snapshotIterator();while(iterator.hasNext())
                        {
                            SootClass sootClass = (SootClass) iterator.next();
//                        System.out.println("sootClass: "+sootClass.getName());
                            if(sootClass.getName().equals(hierarchyList.get(finalI2).getName()))
                            {
                                System.out.println("Inside something at least");
                                if(asList.contains(sootClass))
                                {
                                    if(fsList.contains(sootClass))
                                    {
                                        for(int j = finalI2 - 1; j>=0; j--)
                                        {
                                            fsList.add(hierarchyList.get(j));
                                        }
                                    }
                                }

                                else{
                                    asList.add(sootClass);
                                    List<SootMethod> sootMethodList = sootClass.getMethods();

                                    for(int j=0;j<sootMethodList.size();j++)
                                    {
                                        SootMethod sootMethod = sootMethodList.get(j);
                                        String currentMethodName = sootMethod.getName();
                                        if(currentMethodName.equals("startForeground"))
                                        {
                                            for(int g=finalI2;g<hierarchyList.size()-1;g++)
                                            {
                                                fsList.add(hierarchyList.get(g));
                                            }
                                        }

                                        else
                                        {
                                            if(sootMethod.hasActiveBody())
                                            {
                                                System.out.println("active: "+sootClass.getName()+" | "+sootMethod.getName());
                                                Body body1 = sootMethod.retrieveActiveBody();
                                                final PatchingChain<Unit> unit = body1.getUnits();
                                                for(Iterator<Unit> iterator2 = unit.snapshotIterator(); iterator2.hasNext();)
                                                {
                                                    final Unit u = iterator2.next();
                                                    SootMethod finalSootMethod = sootMethod;
                                                    u.apply(new AbstractStmtSwitch() {
                                                        @Override
                                                        public void caseInvokeStmt(InvokeStmt stmt) {

                                                            InvokeExpr invokeExpr = stmt.getInvokeExpr();

                                                            if(!invokeExpr.getMethod().getName().contains("<init>"))
                                                            {
                                                                if(invokeExpr.getMethod().getName().contains("startForeground"))
                                                                {
                                                                    System.out.println("SF mil gya chora "+sootClass.getName()+" | "+ finalSootMethod.getName());
                                                                    for(int g = i; g>=0; g--)
                                                                    {
                                                                        fsList.add(hierarchyList.get(g));
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
                                                System.out.println("Dead: "+sootClass.getName()+" | "+sootMethod.getName());

                                                SootClass ownerSootClass = sootMethod.getDeclaringClass();
                                                for(int r=0;r<listHL.size();r++)
                                                {
                                                    if(listHL.get(r).get(0).equals(ownerSootClass))
                                                    {
                                                        Iterator iterator2 = Scene.v().getClasses().snapshotIterator();
                                                        while(iterator2.hasNext())
                                                        {
                                                            SootClass jumpSootClass = (SootClass) iterator2.next();
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
                                                                            Body body1 = sootMethod.retrieveActiveBody();
                                                                            final PatchingChain<Unit> units2 = body1.getUnits();
                                                                            for(Iterator<Unit> its = units2.snapshotIterator(); its.hasNext();)
                                                                            {
                                                                                final Unit u = its.next();
                                                                                u.apply(new AbstractStmtSwitch() {
                                                                                    @Override
                                                                                    public void caseInvokeStmt(InvokeStmt stmt) {
                                                                                        InvokeExpr invokeExpr = stmt.getInvokeExpr();

                                                                                        if(!invokeExpr.getMethod().getName().contains("<init>"))
                                                                                        {
                                                                                            if(invokeExpr.getMethod().getName().contains("startForeground"))
                                                                                            {
                                                                                                for(int h = i; h>=0; h--)
                                                                                                {
                                                                                                    fsList.add(hierarchyList.get(h));
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
                        count1++;
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
                            "//home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/com.google.android.apps.youtube.music.apk"
                    };
            soot.Main.main(sootArgs);

//            SootClass sootClass = hierarchyList.get(i);

//            if(asList.contains(sootClass))
//            {
//                if(fsList.contains(sootClass))
//                {
//                    for(int j=i-1;j>=0;j--)
//                    {
//                        fsList.add(hierarchyList.get(j));
//                    }
//                }
//            }

//            else{
//                asList.add(sootClass);
//                List<SootMethod> sootMethodList = sootClass.getMethods();
//
//                for(int j=0;j<sootMethodList.size();j++)
//                {
//                    SootMethod sootMethod = sootMethodList.get(j);
//                    String currentMethodName = sootMethod.getName();
//                    if(currentMethodName.equals("startForeground"))
//                    {
//                        for(int g=i;g<hierarchyList.size()-1;g++)
//                        {
//                            fsList.add(hierarchyList.get(g));
//                        }
//                    }
//
//                    else
//                    {
//                        if(sootMethod.hasActiveBody())
//                        {
//                            System.out.println("active: "+sootClass.getName()+" | "+sootMethod.getName());
//                            Body body = sootMethod.retrieveActiveBody();
//                            final PatchingChain<Unit> unit = body.getUnits();
//                            for(Iterator<Unit> iterator = unit.snapshotIterator(); iterator.hasNext();)
//                            {
//                                final Unit u = iterator.next();
//                                int finalI = i;
//                                u.apply(new AbstractStmtSwitch() {
//                                    @Override
//                                    public void caseInvokeStmt(InvokeStmt stmt) {
//
//                                        InvokeExpr invokeExpr = stmt.getInvokeExpr();
//
//                                        if(!invokeExpr.getMethod().getName().contains("<init>"))
//                                        {
//                                            if(invokeExpr.getMethod().getName().contains("startForeground"))
//                                            {
//                                                List<Type> paramList = invokeExpr.getMethod().getParameterTypes();
//                                                if(paramList.get(0).equals("int") && paramList.get(1).equals("android.app.Notification"))
//                                                {
//                                                    for(int g = finalI; g>=0; g--)
//                                                    {
//                                                        fsList.add(hierarchyList.get(g));
//                                                    }
//                                                }
//                                            }
//                                        }
//
//                                        super.caseInvokeStmt(stmt);
//                                    }
//                                });
//                            }
//                        }
//
//                        int count = 0;
//
//                        if(!sootMethod.hasActiveBody())
//                        {
//                            SootClass ownerSootClass = sootMethod.getDeclaringClass();
//                            for(int r=0;r<listHL.size();r++)
//                            {
//                                if(listHL.get(r).get(0).equals(ownerSootClass))
//                                {
//                                    Iterator iterator = Scene.v().getClasses().snapshotIterator();
//                                    while(iterator.hasNext())
//                                    {
//                                        SootClass jumpSootClass = (SootClass) iterator.next();
//                                        if(jumpSootClass.equals(ownerSootClass))
//                                        {
//                                            List<SootMethod> ownerSootMethodList = jumpSootClass.getMethods();
//                                            for(int w=0;w<ownerSootMethodList.size();w++)
//                                            {
//                                                if(ownerSootMethodList.get(w).equals(sootMethod))
//                                                {
//                                                    sootMethod = ownerSootMethodList.get(w);
//                                                    if(sootMethod.hasActiveBody())
//                                                    {
//                                                        Body body = sootMethod.retrieveActiveBody();
//                                                        final PatchingChain<Unit> units = body.getUnits();
//                                                        for(Iterator<Unit> its = units.snapshotIterator(); its.hasNext();)
//                                                        {
//                                                            final Unit u = its.next();
//                                                            int finalI1 = i;
//                                                            u.apply(new AbstractStmtSwitch() {
//                                                                @Override
//                                                                public void caseInvokeStmt(InvokeStmt stmt) {
//                                                                    InvokeExpr invokeExpr = stmt.getInvokeExpr();
//
//                                                                    if(!invokeExpr.getMethod().getName().contains("<init>"))
//                                                                    {
//                                                                        if(invokeExpr.getMethod().getName().contains("startForeground"))
//                                                                        {
//                                                                            List<Type> paramsList = invokeExpr.getMethod().getParameterTypes();
//                                                                            if(paramsList.get(0).equals("int") && paramsList.get(1).equals("android.app.Notification"))
//                                                                            {
//                                                                                for(int h = finalI1; h>=0; h--)
//                                                                                {
//                                                                                    fsList.add(hierarchyList.get(h));
//                                                                                }
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                    super.caseInvokeStmt(stmt);
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
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
        if(sootClass.hasSuperclass())
        {
            return true;
        }
        else
        {
            return false;
        }
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