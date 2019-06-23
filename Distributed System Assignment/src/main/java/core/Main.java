package core;



import commands.TcpCommandManager;
import data.CommandQueues;
import data.HeartBeatTable;

import data.ProcessClock;
import heartbeat.HeartBeatManager;
import heartbeat.SendHeartBeat;

import network.TcpMessageServerManager;
import network.TcpOutMessageManager;
import network.UdpMessageServerManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class Main {

    public static Vector<String> successorsList;
    public static Vector<String> predecessorsList;


    public static Vector<String> currentMachineList;
    public static Vector<Long> currentMachineListLoginTime;

    public static CommandQueues commandQueues;

    public static int heartBeatPort = 1526;
    public static int heartBeatTime = 300;
    public  static int heartBeatTimeout = 2000;
    public static HeartBeatTable heartBeatTable;

    public static String localHostIP;
    public static ProcessClock localProcessClock;

    public static Boolean development = true;
    public static Logger log;
    //public static String logName = "/home/ec2-user/CPEN/mylogs.log";
    public static String logName = "mylogs.log";

    public static String fs533FileFolder = "/home/ec2-user/CPEN/MyFiles";

    public static boolean processActive;

    public static String masterIPAddress;

    public static ArrayList<String> cacheFile;
    public static boolean cacheFileSaved;
    public static String cacheFileName;

    public static String filePath = "MyFiles";

    public static int inPortNum;
    public static int outPortNum;
    public static int fileTransferPortNUm = 7000;

    public static void main(String[] args) throws Exception {



        log = new Logger(logName);
        log.writeLogLine("***New instance of server process started***");

        WipeManager wipeManager = new WipeManager();
        wipeManager.createFolder(filePath);
        wipeManager.clearFolder(filePath);

        cacheFileSaved = true;

        processActive = true;

        successorsList =  new Vector<>();
        predecessorsList = new Vector<>();
        heartBeatTable = new HeartBeatTable();
        currentMachineList =  new Vector<>();
        currentMachineListLoginTime = new Vector<>();
        cacheFile = new ArrayList<>();

        commandQueues = new CommandQueues();
        localProcessClock =  new ProcessClock();

        localHostIP = InetAddress.getLocalHost().getHostAddress();

        ExecutorService udpMessageServerThread = Executors.newSingleThreadExecutor();
        udpMessageServerThread.execute(new UdpMessageServerManager());

        if(args.length > 0)
        {
            if(args[0].equals("GW"))
            {
                inPortNum = 6000;
                outPortNum = 5000;
                currentMachineList.add(localHostIP);
                currentMachineListLoginTime.add(localProcessClock.getClock());
                masterIPAddress = localHostIP;
            }
            else
            {
                inPortNum = 5000;
                outPortNum = 6000;
                //send Login to the provided IP address
                GateWayManager gateWayManager = new GateWayManager();
                gateWayManager.requestLogin(args[0]);
            }
        }


        ScheduledExecutorService hbThread = Executors.newSingleThreadScheduledExecutor();
        hbThread.scheduleWithFixedDelay(new SendHeartBeat(), 0, heartBeatTime, TimeUnit.MILLISECONDS);

        ExecutorService clientThread = Executors.newSingleThreadExecutor();
        clientThread.execute(new TcpMessageServerManager());

        ExecutorService tcpOutThread = Executors.newSingleThreadExecutor();
        tcpOutThread.execute(new TcpOutMessageManager());

        ExecutorService commandThread = Executors.newSingleThreadExecutor();
        commandThread.execute(new TcpCommandManager());

        while(currentMachineList.size() == 1)
        {
            Thread.sleep(500);
        }

        ScheduledExecutorService hbMonitorThread = Executors.newSingleThreadScheduledExecutor();
        hbMonitorThread.scheduleWithFixedDelay(new HeartBeatManager(), 1500, heartBeatTimeout, TimeUnit.MILLISECONDS);




    }



    public static void writeLog(String message) throws IOException
    {
        if(development) {
            System.out.println( message);
        }

        log.writeLogLine("Sent message: " + message);
    }

}
