package core;


import com.google.gson.Gson;
import data.HeartBeatTable;
import data.UDPMessage;
import heartbeat.SendHeartBeat;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import network.UdpMessageClient;
import network.UdpMessageServerManager;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class Main {

    public static Vector<String> successorsList;
    public static Vector<String> predecessorsList;

    public static Vector<String> currentMachineList;
    public static Vector<String> currentMachineListLoginTime;

    public static int heartBeatPort = 1526;
    public static int heartBeatTime = 5000;
    public static HeartBeatTable heartBeatTable;

    public static Boolean development = true;
    public static Logger log;

    public static boolean processActive;



    public static void main(String[] args) throws Exception {



        log = new Logger("mylogs.log");
        log.writeLogLine("***New instance of server process started***");

        processActive = true;
        successorsList =  new Vector<>();
        predecessorsList = new Vector<>();
        heartBeatTable = new HeartBeatTable();
        currentMachineList =  new Vector<>();
        currentMachineListLoginTime = new Vector<>();

        if(args.length > 0)
        {
            if(args[0].equals("GW"))
            {
                Date date =  new Date();
                currentMachineList.add(InetAddress.getLocalHost().getHostAddress());
                currentMachineListLoginTime.add(date.toString());
            }
            else
            {
                //send Login to the provided IP address
                GateWayManager gateWayManager = new GateWayManager();
                gateWayManager.requestLogin(args[0]);
            }
        }


        ExecutorService es2 = Executors.newSingleThreadExecutor();
        es2.execute(new UdpMessageServerManager());

        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleWithFixedDelay(new SendHeartBeat(), 0, heartBeatTime, TimeUnit.MILLISECONDS);



    }



    public static void writeLog(String message) throws IOException
    {
        if(development) {
            System.out.println( message);
        }

        log.writeLogLine("Sent message: " + message);
    }

}
