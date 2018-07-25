package InstrumentAPK;

import java.io.File;

public class NormalJAVATesting {

    private static final String OUTPUT_SOOT_DIR = "/media/dhruv2601/Carseat/sootOutput/";

    public static void main(String args[])
    {
        File folder = new File(OUTPUT_SOOT_DIR);
        File[] listOfFiles = folder.listFiles();
        for(int i=0;i<listOfFiles.length;i++)
        {
            System.out.println("folder:: "+listOfFiles[i].toString());
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

}
