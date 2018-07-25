package InstrumentAPK;

import soot.*;
import soot.options.Options;

import java.util.Iterator;
import java.util.Map;

public class FBInstrumentGPS  {

    private static final String RESULTFILE = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/outputFile.txt";
    private static String temp = "jtp.myInstrumenter";
    private static String jtpInstrumenter;

    public static void main(String[] args)
    {
        soot.G.reset();

        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);

        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);

        soot.Main.main(args);
    }
}
