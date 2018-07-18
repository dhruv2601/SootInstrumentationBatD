package InstrumentAPK;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ScriptForSoot {

    private static int numSS;               //total no. of times startService called
    private static int numSF;               //total no. of times startForeground called
    private static int numSFS;              //total no. of times startForegroundService called
    private static int totalNumServices;    //total no. of services
    private static int totalBackgServices;  //total no. of background services
    private static int totalForegServices;  //total no. of foreground services

    private static final String TAB_DELIMITER = "\t";
    private static final String NEW_LINE_SEPERATOR = "\n";

    private static final String THUNDER_OUTPUT = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/result/ThunderOutput.csv";

    public static void main(String[] args)
    {
        int testCounter = 0;
        String csvFile = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/refData.csv";
        String OUTER_DIR = "/media/dhruv2601/Carseat/TopFreeAppsDataSet/";   //is appended to APK path later

        CSVReader reader = null;

        try {
            reader = new CSVReader(new FileReader(csvFile));
            String line[];

            while((line = reader.readNext())!=null)
            {
                if(testCounter==3)
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


//                System.out.println("Output = "+line[0]);
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
                        "-process-multiple-dex",
                        "-android-jars",
                        Constants.ANDROID_JAR,
                        "-process-dir",
                        APK_DIR
                };

                int arr[] = new int[3];
                arr = AndroidInstrument.main(sootArgs);
                numSS = arr[0]; numSF = arr[1]; numSFS = arr[2];
                totalNumServices = Integer.parseInt(String.valueOf(numService));
                totalForegServices = numSF;
                totalBackgServices = totalNumServices - totalForegServices;

                FileWriter thunderWriter = null;

                thunderWriter = new FileWriter(THUNDER_OUTPUT);

                thunderWriter.append(serialNum);
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(category);
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(packageName);
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(targetSDK);
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(minSDK);
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(String.valueOf(totalForegServices));
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(String.valueOf(totalBackgServices));
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(String.valueOf(numSS));
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(String.valueOf(numSF));
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(String.valueOf(numSFS));
                thunderWriter.append(TAB_DELIMITER);
                thunderWriter.append(String.valueOf(totalNumServices));
                thunderWriter.append(NEW_LINE_SEPERATOR);

                thunderWriter.flush();
                thunderWriter.close();

                testCounter++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}