package data;

import java.io.*;
import java.util.ArrayList;

public class ReadWriteManager {



    public ArrayList <String> cacheFile (String fileName) {

        BufferedReader bw = null;
        ArrayList<String> lineList = new ArrayList <>();

        try {
            bw = new BufferedReader(new FileReader(fileName));
            String line = bw.readLine();
            while(line != null)
            {
                //System.out.println(line);
                lineList.add(line);
                line = bw.readLine();

            }

            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineList;
    }

    public void writeFile (String fileName, ArrayList <String> phrase) {

        try {
            FileWriter writer = new FileWriter (fileName,false);
            for (String str:phrase){
                writer.write(str);}
            writer.close();
        }

        catch (IOException e) {
            e.printStackTrace(); }
    }

}
