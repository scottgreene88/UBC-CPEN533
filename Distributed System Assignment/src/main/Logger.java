package main;

import java.io.*;

public class Logger {

    String fileName;

    public Logger (String fileName) throws IOException {

        this.fileName=fileName;
        File myFile = new File("mylogs.log");

    }

    public void writeLogLine(String input) throws IOException {
        FileWriter bw = new FileWriter("mylogs.log", true);
        InetAddress host = InetAddress.getLocalHost();
        Date date = new Date();
        bw.append(host + " " + date + " " + input + System.lineSeparator());
        bw.close();
    }

}





