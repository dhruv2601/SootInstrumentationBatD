package InstrumentAPK;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ScriptForSoot {

    private static int numSS;               //total no. of times startService called
    private static int numSF;               //total no. of times startForeground called
    private static int numSFS;              //total no. of times startForegroundService called
    private static int totalNumServices;    //total no. of services
    private static int totalBackgServices;  //total no. of background services
    private static int totalForegServices;  //total no. of foreground services
    private static int isGPSCalledViaForeground = 0; // if the GPS is called via a foreground service in whole of the app

    private static final String TAB_DELIMITER = "\t";
    private static final String NEW_LINE_SEPERATOR = "\n";

    private static final String THUNDER_OUTPUT = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/ThunderOutput.csv";
    private static final String OUTPUT_SOOT_DIR = "/media/dhruv2601/Carseat/sootOutput/";

    private static int foregroundMention =0;
    private static int GPSMention = 0;

    public static void main(String[] args)
    {
        int testCounter = 0;
        String csvFile = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/refData.csv";
        String OUTER_DIR = "/media/dhruv2601/Carseat/TopFreeAppsDataSet/";   // is appended to APK path later

        CSVReader reader = null;

        try {
            reader = new CSVReader(new FileReader(csvFile));
            String line[];

            while((line = reader.readNext())!=null)
            {
                numSS=0; numSFS = 0; numSF = 0;
                if(testCounter==1)
                {
                    break;
                }

                int tabCounter = 0;
                StringBuilder serialNum = new StringBuilder();
                StringBuilder category = new StringBuilder();
                StringBuilder packageName = new StringBuilder();
                StringBuilder targetSDK = new StringBuilder();
                StringBuilder minSDK = new StringBuilder();
                StringBuilder numService = new StringBuilder();


                System.out.println("\n");
                String s = line[0];
                StringBuilder temp = new StringBuilder();
                for(char ch: s.toCharArray())
                {
                    if(ch == '\t')
                    {
                        tabCounter++;
                    }
                    else
                    {
                        if(tabCounter==0)
                        {
                            serialNum.append(ch);
                        }
                        if(tabCounter==1)
                        {
                            category.append(ch);
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
                            numService.append(ch);
                        }
                    }
                }

//                System.out.println(serialNum+" "+category+" "+packageName+" "+targetSDK+" "+minSDK+" "+numService);
                String temp1 = OUTER_DIR+category+"/";
                String temp2 = temp1+packageName;
                String APK_DIR = temp2+".apk";
                System.out.println(APK_DIR);

                String sootArgs[] = new String[]{
                        String.valueOf(serialNum),
                        "-process-multiple-dex",
                        "-android-jars",
                        Constants.ANDROID_JAR,
                        "-d",
                        "/media/dhruv2601/Carseat/sootOutput/",
                        "-process-dir",
                        APK_DIR
                };

                int arr[] = new int[3];
                try
                {
                    arr = AndroidInstrument.main(sootArgs);
                    numSS = arr[0]; numSF = arr[1]; numSFS = arr[2];
                }
                catch (RuntimeException e)
                {
                    System.out.println("This APK was not able to be processed -  "+ e.getMessage());
                    numSS = 0; numSF = 0; numSFS = 0;
                }

                totalNumServices = Integer.parseInt(String.valueOf(numService));
                totalForegServices = numSF;
                totalBackgServices = totalNumServices - totalForegServices;

                // -- starting with file crawling

                File folder = new File(OUTPUT_SOOT_DIR);
                File[] listOfFiles = folder.listFiles();

                if (listOfFiles != null) {
                    System.out.println("no. of files to crawl:  "+listOfFiles.length);
                    for(int i=0;i<listOfFiles.length;i++)
                    {
                        foregroundMention = 0; GPSMention = 0; isGPSCalledViaForeground = 0;
                        int flag = 0;
                        if(listOfFiles[i].isFile())
                        {
                            File jimpleFile = listOfFiles[i];
                            Scanner scanner = new Scanner(jimpleFile);
                            while(scanner.hasNext()){
                                String jLine = scanner.nextLine();
                                for(int j=0;j<Constants.GPSMethodList.length;j++)
                                {
                                    if(jLine.contains(Constants.GPSMethodList[j]))
                                    {
                                        GPSMention = 1;
                                    }
                                    if(jLine.contains("startForeground"))
                                    {
                                        foregroundMention = 1;
                                    }
                                    if(GPSMention == 1 && foregroundMention == 1)
                                    {
                                        // add 1 to the csv ke column mein  and break   and flag=1
                                        isGPSCalledViaForeground = 1;
                                        flag = 1;
                                        break;
                                    }
                                }
                                if(flag==1)
                                {
                                    break;      // to exit while and this APK altogether
                                }
                            }
                        }

                        if(flag==0)
                        {
                            if(GPSMention == 0 && foregroundMention == 0)
                            {
                                isGPSCalledViaForeground = 0;
                            }
                        }
                    }
                }

                // -- ending file calling, proceed to write

                try(BufferedWriter thunderWriter = new BufferedWriter(new FileWriter(THUNDER_OUTPUT, true)))
                {
                    thunderWriter.write(String.valueOf(serialNum));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(category));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(packageName));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(targetSDK));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(minSDK));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(totalForegServices));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf((totalBackgServices)));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(numSS));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf((numSF)));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(numSFS));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(totalNumServices));
                    thunderWriter.write(TAB_DELIMITER);
                    thunderWriter.write(String.valueOf(isGPSCalledViaForeground));
                    thunderWriter.write(NEW_LINE_SEPERATOR);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                testCounter++;

                // -- deleting the soot output folder, i.e., jimple files
                File sootOutputFolder = new File(OUTPUT_SOOT_DIR);
                deleteFolder(sootOutputFolder);

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