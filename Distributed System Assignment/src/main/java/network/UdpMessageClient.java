package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import core.*;

public class UdpMessageClient
{

    private int portNum;
    private InetAddress ipAddress;


    public UdpMessageClient(int portNumIn, InetAddress ipAddressIn) throws IOException
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

        //if(!message.contains("HB")) {
            //Main.writeLog("Send message: " + message + " to IP: " + ipAddress);
        //}

    }

}
