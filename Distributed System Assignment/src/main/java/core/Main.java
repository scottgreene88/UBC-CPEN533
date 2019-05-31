package core;

import java.io.IOException;
import java.net.InetAddress;


import commands.commandServerManager;
import  heartbeat.heartBeatManager;

public class Main {

    public static Boolean development = true;
    public static Logger log;



    public static void main(String[] args) throws IOException {

        log = new Logger("mylogs.log");
        log.writeLogLine("New instance of server process started");


        InetAddress host = InetAddress.getLocalHost();

        //This stuff below needs to be set up before the rest of the operations can get started
        // will need to do the request for join and gather this info here.
        int leftPortForward = 1234;
        int rightPortForward = 5678;

        int commandServerPort = 1526;


        //This is the standard operating portion of the process
        Thread hbThread = new Thread(new heartBeatManager(leftPortForward,rightPortForward,host, host));
        hbThread.start();

        Thread csmThread = new Thread(new commandServerManager(commandServerPort));
        csmThread.start();

    }


}
