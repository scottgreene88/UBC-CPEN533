package main;

import java.io.IOException;

public class Main {

    public static Boolean development = true;
    public static Logger log;

    public static void main(String[] args) throws IOException {

       Logger log = new Logger("mylogs.log");
        log.writeLogLine("blabla");
        log.writeLogLine("test");


    }
}
