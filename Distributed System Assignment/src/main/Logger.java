package main;

import java.io.*;

public class Logger {

    String fileName;

    public Logger (String fileName) throws IOException {

        this.fileName=fileName;
        File myFile = new File("mylogs.log");
        myFile.createNewFile();
    }

    public void writeLogLine(String input) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("mylogs.log", true));
        bw.append(input + System.lineSeparator());
        bw.close();
    }

}





