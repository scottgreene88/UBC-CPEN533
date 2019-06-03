package network;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;

import core.*;

public class TcpMessageClient {

    private String ipAddress;
    private int portNum;
    private Socket socket;
    private Vector<String> ipVector;

    public TcpMessageClient(int portNumIn, String ipAddressIn) throws IOException
    {
        this.ipAddress = ipAddressIn;
        this.portNum = portNumIn;

    }

    public String sendSingleMessage(String message) throws IOException
    {

        boolean result = false;
        String response = "";

        try {
            this.socket = new Socket(ipAddress, portNum);
            socket.setSoTimeout(1000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            out.println(message);

            Scanner in = new Scanner(socket.getInputStream());

            while(in.hasNextLine()) {
                response = in.nextLine();
            }

            if(OLDMain.development) {
                System.out.println("Client sent: " + message + " to: " + ipAddress + " responded: " + response);
            }
            OLDMain.log.writeLogLine("Client sent: " + message + " to: " + ipAddress + " responded: " + response);

        }
        catch(java.net.SocketTimeoutException e)
        {
            response = "FAILED";
            if(OLDMain.development) {
                System.out.println("Socket Timeout Exception: " + e.getMessage());
            }

            OLDMain.log.writeLogLine("Socket Timeout Exception: " + e.getMessage());

        }
        catch (Exception e)
        {
            response = "FAILED";
            if(OLDMain.development) {
                System.out.println("Socket Exception: " + e.getMessage());
            }

            OLDMain.log.writeLogLine("Socket Exception: " + e.getMessage());
        }finally {
            try { socket.close(); } catch (IOException e) {

                if(OLDMain.development) {
                    System.out.println("Socket Exception: " + e.getMessage());
                }

                OLDMain.log.writeLogLine("Socket Exception: " + e.getMessage());

            }

        }


        return response;



    }


}
