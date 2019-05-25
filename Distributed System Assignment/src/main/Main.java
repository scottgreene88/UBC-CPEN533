package main;

import java.io.IOException;
import java.net.InetAddress;

import network.*;
import  heartbeat.sendHeartBeat;

public class Main {

    public static Boolean development = true;
    public static Logger log;

    public static void main(String[] args) throws IOException {

       Logger log = new Logger("mylogs.log");
        log.writeLogLine("blabla");
        log.writeLogLine("test");

        InetAddress host = InetAddress.getLocalHost();

        System.out.println("Starting to ping");

        udpMessageServer server =  new udpMessageServer(1234);

        sendHeartBeat sender = new sendHeartBeat(1234,1234,host, host);




    }


}
