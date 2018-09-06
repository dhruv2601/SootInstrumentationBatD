package InstrumentAPK;

import java.io.*;

public class TestingJimple {

    // class to crawl all jimple files to search and record for instances of the functions

    public static void main(String[] args)
    {
        String JIMIPLE_DIR = "/media/dhruv2601/Carseat/Top500FreeAppDataset/jimple_output/";
        String OUTPUT_DIR = "/media/dhruv2601/Carseat/Top500FreeAppDataset/velvet_output.txt";
        String TEMP_OUT_DIR = "/media/dhruv2601/Carseat/Top500FreeAppDataset/temp_output.txt";

        File folder = new File(JIMIPLE_DIR);

        File[] listFiles = folder.listFiles();

        if(listFiles != null)
        {
            System.out.println("No. of files to crawl: "+listFiles.length);
            for(int i=0;i<listFiles.length;i++)
            {
                if(listFiles[i].isFile())
                {
                    String fileName = listFiles[i].getAbsolutePath();
                    System.out.println("Analyzing "+listFiles[i].getName()+" file");
                    try {
                        FileReader fileReader = new FileReader(fileName);
                        String jLine = null;
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        BufferedWriter thunderWriter = new BufferedWriter(new FileWriter(OUTPUT_DIR, true));

                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(TEMP_OUT_DIR, true));

                        while((jLine = bufferedReader.readLine())!=null)
                        {
                            bufferedWriter.write(jLine+"\n");
                            if(jLine.contains("startService"))
                            {
                                thunderWriter.write("startService  "+listFiles[i].getName()+"\n");
                            }
                            if(jLine.contains("stopService"))
                            {
                                thunderWriter.write("stopService  "+listFiles[i].getName()+"\n");
                            }
                            if(jLine.contains("startForeground"))
                            {
                                thunderWriter.write("startForeground  "+listFiles[i].getName()+"\n");
                            }
                            if(jLine.contains("stopForeground"))
                            {
                                thunderWriter.write("stopForeground  "+ listFiles[i].getName()+"\n");
                            }
                        }
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}