package InstrumentAPK;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class FBIscriptSoot {

    private static final String TAB_DELIMITER = "\t";
    private static final String NEW_LINE_SEPERATOR = "\n";

    private static final String THUNDER_OUTPUT = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/ThunderOutputTop500.csv";
    private static final String OUTPUT_SOOT_DIR = "/media/dhruv2601/Carseat/sootOutput/";

    private static int numForeground = 0;
    private static int numBackground = 0;
    private static boolean isForeground = false;
    private static boolean isBackground = false;

    private static boolean isService = false;

    private static int numGPSBackground = 0;
    private static int numGPSForeground = 0;

    public static void main(String[] args)
    {
        int testCounter = 0;

        String csvFile = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/refData.csv";
//        String OUTER_DIR = "/media/dhruv2601/Carseat/Top500FreeAppDataset/data_extend/";   // is appended to APK path later
        String OUTER_DIR = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/";   // is appended to APK path later

        CSVReader reader = null;

        try {
            reader = new CSVReader(new FileReader(csvFile));
            String line[];

            while((line = reader.readNext())!=null)
            {
                int tabCounter = 0;
                StringBuilder serialNum = new StringBuilder();
                StringBuilder packageName = new StringBuilder();
                StringBuilder targetSDK = new StringBuilder();
                StringBuilder minSDK = new StringBuilder();
                StringBuilder maxSDK = new StringBuilder();

                String s = line[0];
                for(char ch: s.toCharArray())
                {
                    if(ch=='\t')
                    {
                        tabCounter++;
                    }
                    else
                    {
                        if(tabCounter==1)
                        {
                            serialNum.append(ch);
                        }
                        if(tabCounter==2)
                        {
                            packageName.append(ch);
                        }
                        if(tabCounter==3)
                        {
                            targetSDK.append(ch);
                        }
                        if(tabCounter==4)
                        {
                            minSDK.append(ch);
                        }
                        if(tabCounter==5)
                        {
                            maxSDK.append(ch);
                        }
                    }
                }

                String temp1 = OUTER_DIR+packageName;
                String APK_DIR = temp1 + ".apk";
                System.out.println(APK_DIR);

                String sootArgs[] = new String[]{
                        "-process-multiple-dex",
                        "-android-jars",
                        Constants.ANDROID_JAR,
                        "-d",
                        "/media/dhruv2601/Carseat/sootOutput/",
                        "-process-dir",
                        APK_DIR
                };

                FBInstrumentGPS.main(sootArgs);

                File folder = new File(OUTPUT_SOOT_DIR);
                File[] listOfFiles = folder.listFiles();

                if(listOfFiles != null)
                {
                    System.out.println("No. of files to crawl:  "+listOfFiles.length);
                    for(int i = 0; i<listOfFiles.length; i++)
                    {
                        int flag = 0;
//                        numBackground = 0; numForeground = 0;
//                        numGPSBackground = 0; numGPSForeground = 0;

                        isBackground = false; isForeground = false; isService = false;

                        if(listOfFiles[i].isFile())
                        {
                            isService = false;
                            isForeground = false;
                            isBackground = false;

                            File jimpleFile = listOfFiles[i];
                            Scanner scanner = new Scanner(jimpleFile);

                            String fileName = listOfFiles[i].getAbsolutePath();
                            FileReader fileReader = new FileReader(fileName);
                            String jLine = null;

                            BufferedReader bufferedReader = new BufferedReader(fileReader);

                            while((jLine = bufferedReader.readLine())!=null)
                            {
                                if((jLine.contains("extends android.app.Service")
                                        || jLine.contains("extends android.app.IntentService"))
                                        &&(!jLine.contains("android.support.")))
                                {
                                    System.out.println("jLine: "+jLine);
                                    isService = true;
                                }

                                if(jLine.contains("startForeground") && !jLine.contains("startForegroundService") && isService)
                                {
                                    System.out.println("CONTAINS startForeground");
                                    isForeground = true;
                                    isBackground = false;
                                }
                            }

                            if(isService && !isForeground)
                            {
                                isBackground = true;
                            }

                            if(isBackground)
                            {
                                numBackground++;
                            }
                            else if(isForeground)
                            {
                                numForeground++;
                            }

                            String fileName1 = listOfFiles[i].getAbsolutePath();
                            FileReader fileReader1 = new FileReader(fileName1);
                            String jLine1 = null;

                            BufferedReader bufferedReader1 = new BufferedReader(fileReader1);

                            while((jLine1 = bufferedReader1.readLine())!=null)
                            {
                                for(int j=0;j<Constants.GPSMethodList.length; j++)
                                {
                                    if(jLine1.contains(Constants.GPSMethodList[j]))
                                    {
                                        if(isForeground)
                                        {
                                            numGPSForeground++;
                                            flag = 1;
                                        }
                                        if(isBackground)
                                        {
                                            numGPSBackground++;
                                            flag = 1;
                                        }
                                    }
                                    if(flag==1)
                                    {
                                        break;
                                    }
                                }
                                if(flag==1)
                                {
                                    break;
                                }
                            }
                            bufferedReader.close();
                            bufferedReader1.close();
                        }

                    }
                }

                try(BufferedWriter thunderWriter = new BufferedWriter(new FileWriter(THUNDER_OUTPUT, true)))
                {
                    thunderWriter.write(String.valueOf(serialNum));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(packageName));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(targetSDK));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(minSDK));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(maxSDK));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf((numBackground)));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(numForeground));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf((numGPSBackground)));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(numGPSForeground));
                    thunderWriter.write(NEW_LINE_SEPERATOR);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                System.out.println("Total foreground service: "+numForeground+" total GPSFromForeground: "+numGPSForeground);

                System.out.println("Back: "+numBackground+" Fore: "+numForeground+
                        " GPSFore: "+numGPSForeground+" GPSBack: "+numGPSBackground);

                testCounter++;

                File sootOutput = new File(OUTPUT_SOOT_DIR);
//                deleteFolder(sootOutput);

                try {
                    TimeUnit.SECONDS.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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