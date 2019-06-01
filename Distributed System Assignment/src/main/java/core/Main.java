package core;

import java.io.IOException;
import java.net.InetAddress;


import commands.CommandServerManager;
import data.CurrentNeighbours;
import heartbeat.HeartBeatManager;

public class Main {

    public static Boolean development = true;
    public static Logger log;
    public static int commandServerPort = 1526;
    public  static CurrentNeighbours neighbours;

    public static void main(String[] args) throws IOException {

        log = new Logger("mylogs.log");
        log.writeLogLine("***New instance of server process started***");

        neighbours = new CurrentNeighbours();


        //This stuff below needs to be set up before the rest of the operations can get started
        // will need to do the request for join and gather this info here.
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
