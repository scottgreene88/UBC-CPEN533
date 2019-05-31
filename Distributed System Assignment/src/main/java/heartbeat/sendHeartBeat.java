package main.java.heartbeat;

import main.java.network.udpMessageClient;
import main.*;

import java.io.IOException;
import java.net.InetAddress;

public class sendHeartBeat implements Runnable{


    private int rightPortNum;
    private int leftPortNum;
    private InetAddress leftIpAddress;
    private InetAddress rightIpAddress;


    public sendHeartBeat(int leftPortNum, int rightPortNum, InetAddress leftIpAddress, InetAddress rightIpAddress)
    {
        this.leftPortNum = leftPortNum;
        this.rightPortNum = rightPortNum;
        this.leftIpAddress = leftIpAddress;
        this.rightIpAddress = rightIpAddress;
    }

    public void run()
    {
        try
        {
            sendBeats();

        }catch(Exception e)
        {
            if(Main.development) {
                System.out.println("Exception from sendHB: " + e.getMessage());
            }
            try {Main.log.writeLogLine("Exception sendHB: " + e.getMessage());}
            catch(IOException e2) {System.out.println("Exception from logger: " + e2.getMessage()); }
        }

    }


    private void sendBeats() throws IOException
    {
        String hbMessage = "Beat";

        udpMessageClient myLeftForward =  new udpMessageClient(leftPortNum,leftIpAddress);
        udpMessageClient myRightForward =  new udpMessageClient(rightPortNum,rightIpAddress);

        myLeftForward.sendMessage(hbMessage + " 1");
        myRightForward.sendMessage(hbMessage + " 2");

    }

}
