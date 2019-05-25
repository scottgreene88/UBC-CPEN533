package heartbeat;

import network.udpMessageClient;
import main.*;

import java.io.IOException;
import java.net.InetAddress;

public class sendHeartBeat {

    private long hbWaitTime = 2000;
    private int rightPortNum;
    private int leftPortNum;
    private InetAddress leftIpAddress;
    private InetAddress rightIpAddress;

    private String hbMessage = "Beat";

    public sendHeartBeat(int leftPortNum, int rightPortNum, InetAddress leftIpAddress, InetAddress rightIpAddress)
    {
        this.leftPortNum = leftPortNum;
        this.rightPortNum = rightPortNum;
        this.leftIpAddress = leftIpAddress;
        this.rightIpAddress = rightIpAddress;
    }

    public void updateTargetMachines(int newLeftPortNum, int newRightPortNum, InetAddress newLeftIpAddress, InetAddress newRightIpAddress)
    {
        this.leftPortNum = newLeftPortNum;
        this.rightPortNum = newRightPortNum;
        this.leftIpAddress = newLeftIpAddress;
        this.rightIpAddress = newRightIpAddress;
    }

    public void sendBeats() throws IOException
    {
        udpMessageClient myLeftForward =  new udpMessageClient(leftPortNum,leftIpAddress);
        udpMessageClient myRightForward =  new udpMessageClient(rightPortNum,rightIpAddress);

        myLeftForward.sendMessage(hbMessage);
        myRightForward.sendMessage(hbMessage);

        try
        {
            Thread.sleep(hbWaitTime);

        }catch(InterruptedException e)
        {
            if(Main.development) {
                System.out.println("Exception: " + e.getMessage());
            }

            Main.log.writeLogLine("Exception: " + e.getMessage());
        }

    }

}
