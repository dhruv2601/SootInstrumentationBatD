package InstrumentAPK;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ScriptForSoot {

    public static void main(String[] args)
    {
        String csvFile = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/refData.csv";

        CSVReader reader = null;

        try {
            reader = new CSVReader(new FileReader(csvFile));
            String line[];

            while((line = reader.readNext())!=null)
            {
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
                System.out.println(serialNum+" "+category+" "+packageName+" "+targetSDK+" "+minSDK+" "+numService);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}