package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import core.*;

public class udpMessageClient
{

    private int portNum;
    private InetAddress ipAddress;


    public udpMessageClient(int portNumIn, InetAddress ipAddressIn) throws IOException
    {
        this.portNum = portNumIn;
        this.ipAddress = ipAddressIn;


    }

    public void sendMessage(String message) throws IOException
    {
        DatagramSocket ds = new DatagramSocket();

        byte buf[] = null;

        buf = message.getBytes();

        DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ipAddress, portNum);

        ds.send(DpSend);

        ds.close();

        if(Main.development) {
            System.out.println("Sent message: " + message);
        }

        Main.log.writeLogLine("Sent message: " + message);
    }

}
