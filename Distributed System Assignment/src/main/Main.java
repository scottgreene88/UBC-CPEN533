package main;

import java.io.IOException;
import java.net.InetAddress;

import network.*;
import  heartbeat.heartBeatManager;

public class Main {

    public static Boolean development = true;
    public static Logger log;

    public static void main(String[] args) throws IOException {

        log = new Logger("mylogs.log");
        log.writeLogLine("New instance of server process started");


        InetAddress host = InetAddress.getLocalHost();

        System.out.println("Starting to ping");

        heartBeatManager hbManager =  new heartBeatManager(1234,5678,host, host);
        hbManager.startHb();


    }


}
