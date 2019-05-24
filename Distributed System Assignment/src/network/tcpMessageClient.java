package network;

import java.io.*;
import java.net.*;
import java.util.Vector;

import main.*;

public class tcpMessageClient {

    private String ipAddress;
    private int portNum;
    private Socket socket;
    private Vector<String> ipVector;

    tcpMessageClient(int portNumIn, String ipAddressIn) throws IOException
    {
        this.ipAddress = ipAddressIn;
        this.portNum = portNumIn;

        try
        {
            this.socket = new Socket(ipAddress, portNum);
            socket.setSoTimeout(10000);
        }
        catch (SocketException e)
        {
            if(Main.development) {
                System.out.println("Socket Exception: " + e.getMessage());
            }

            Main.log.writeLogLine("Socket Exception: " + e.getMessage());
        }

    }


}
