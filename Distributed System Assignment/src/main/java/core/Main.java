package core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;


import commands.CommandServerManager;
import data.CurrentNeighbours;
import data.Machine;
import data.MachineList;
import heartbeat.HeartBeatManager;

public class Main {

    public static Boolean development = true;
    public static Logger log;
    public static int commandServerPort = 1526;
    public  static CurrentNeighbours neighbours;
    public static boolean isGatewayNode;
    public static Machine thisMachine;

    public static void main(String[] args) throws Exception {

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

        neighbours = new CurrentNeighbours();

        int leftPortForward;
        int rightPortForward;
        InetAddress leftIpAddressForward;
        InetAddress rightIpAddressForward;


        Thread csmThread = new Thread(new CommandServerManager(commandServerPort));
        csmThread.start();

        while(true) {

            leftPortForward = neighbours.leftPortNumForward;
            rightPortForward = neighbours.rightPortNumForward;
            leftIpAddressForward = neighbours.leftIpAddressForward;
            rightIpAddressForward = neighbours.rightIpAddressForward;

            //This is the standard operating portion of the process
            Thread hbThread = new Thread(new HeartBeatManager(leftPortForward, rightPortForward, leftIpAddressForward, rightIpAddressForward));
            hbThread.start();
            try {
                hbThread.join();

            }catch (InterruptedException e)
            {
                if (Main.development) {
                    System.out.println("Exception from hbThread join: " + e.getMessage());
                }
                Main.log.writeLogLine("Exception hbThread join: " + e.getMessage());
            }
        }



    }

}
