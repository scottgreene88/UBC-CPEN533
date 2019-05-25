package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import main.*;

public class udpMessageClient
{

    private int portNum;
    private InetAddress ipAddress;
    private DatagramSocket ds;

    public udpMessageClient(int portNumIn, InetAddress ipAddressIn) throws IOException
    {
        this.portNum = portNumIn;
        this.ipAddress = ipAddressIn;

        ds = new DatagramSocket();
    }

    public void sendMessage(String message) throws IOException
    {
        byte buf[] = null;

        buf = message.getBytes();

        DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ipAddress, portNum);

        ds.send(DpSend);

        if(Main.development) {
            System.out.println("Sent message: " + message);
        }

        Main.log.writeLogLine("Sent message: " + message);
    }


}
