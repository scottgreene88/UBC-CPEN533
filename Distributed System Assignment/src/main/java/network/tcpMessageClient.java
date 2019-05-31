package network;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;

import core.*;

public class tcpMessageClient {

    private String ipAddress;
    private int portNum;
    private Socket socket;
    private Vector<String> ipVector;

    public tcpMessageClient(int portNumIn, String ipAddressIn) throws IOException
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

    public boolean sendSingleMessage(String message) throws IOException
    {

        boolean result = false;
        String response = "";

        try {
            this.socket = new Socket(ipAddress, portNum);
            socket.setSoTimeout(10000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            out.println(message);

            Scanner in = new Scanner(socket.getInputStream());

            while(in.hasNextLine()) {
                response = in.nextLine();
            }

            if(Main.development) {
                System.out.println("Client sent: " + message + " to: " + ipAddress + " responded: " + response);
            }
            Main.log.writeLogLine("Client sent: " + message + " to: " + ipAddress + " responded: " + response);

        }
        catch(java.net.SocketTimeoutException e)
        {
            if(Main.development) {
                System.out.println("Socket Timeout Exception: " + e.getMessage());
            }

            Main.log.writeLogLine("Socket Timeout Exception: " + e.getMessage());

        }
        catch (Exception e)
        {
            if(Main.development) {
                System.out.println("Socket Exception: " + e.getMessage());
            }

            Main.log.writeLogLine("Socket Exception: " + e.getMessage());
        }finally {
            try { socket.close(); } catch (IOException e) {

                if(Main.development) {
                    System.out.println("Socket Exception: " + e.getMessage());
                }

                Main.log.writeLogLine("Socket Exception: " + e.getMessage());

            }

        }

        if(response.contains("ACK"))
        {
            result = true;
        }


        return result;



    }


}
