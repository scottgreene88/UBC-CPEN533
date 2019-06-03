package core;

import java.net.InetAddress;


import commands.CommandServerManager;
import data.HeartBeatTable;
import data.Machine;
import heartbeat.OLDHeartBeatManager;

public class OLDMain {

    public static Boolean development = true;
    public static Logger log;
    public static int commandServerPort = 1526;
    public  static HeartBeatTable neighbours;
    public static boolean isGatewayNode;
    public static Machine thisMachine;

    public static boolean processActive;

    public static void Main(String[] args) throws Exception {

        log = new Logger("mylogs.log");
        log.writeLogLine("***New instance of server process started***");


        if(args.length != 0)
        {
            if(args[0].equals("GW"))
            {
                isGatewayNode = true;
            }
        }
        else
        {
            isGatewayNode = false;
        }

        MachineListManager mlManager = new MachineListManager();
        if(isGatewayNode)
        {
            thisMachine = mlManager.getThisMachine();
            mlManager.localMachineList.add(thisMachine);
        }

        //This stuff should be used once generalized gateway requesting is implemented
        /*
        MachineListManager mlManager = new MachineListManager();
        Machine gateWay = mlManager.findGatewayNode();
        thisMachine = mlManager.getThisMachine();
        machineList.localMachineList.add(thisMachine);
        */


        //This stuff below needs to be set up before the rest of the operations can get started
        // will need to do the request for join and gather this info here.

        neighbours = new HeartBeatTable();

        int leftPortForward;
        int rightPortForward;
        InetAddress leftIpAddressForward;
        InetAddress rightIpAddressForward;


        Thread csmThread = new Thread(new CommandServerManager(commandServerPort));
        csmThread.start();

        while(true) {

            leftPortForward = 1;
            rightPortForward = 1;
            leftIpAddressForward = InetAddress.getLocalHost();
            rightIpAddressForward = InetAddress.getLocalHost();

            //This is the standard operating portion of the process
            Thread hbThread = new Thread(new OLDHeartBeatManager(leftPortForward, rightPortForward, leftIpAddressForward, rightIpAddressForward));
            hbThread.start();
            try {
                hbThread.join();

            }catch (InterruptedException e)
            {
                if (OLDMain.development) {
                    System.out.println("Exception from hbThread join: " + e.getMessage());
                }
                OLDMain.log.writeLogLine("Exception hbThread join: " + e.getMessage());
            }
        }



    }

}
