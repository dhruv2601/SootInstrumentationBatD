package InstrumentAPK;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SootInheritanceTest extends SceneTransformer {


    public static void main()
    {

    }

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        System.out.println("internalTransform starts");

    }
}
