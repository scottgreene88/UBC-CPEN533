package network;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;

import core.*;

public class TcpMessageClient implements Callable {

    private String ipAddress;
    private int portNum;
    private Socket socket;
    private Vector<String> ipVector;
    private  String message;

    public TcpMessageClient(int portNumIn, String ipAddressIn, String message)
    {
        this.ipAddress = ipAddressIn;
        this.portNum = portNumIn;
        this.message = message;
    }

    public Vector<String> call()
    {

        Vector<String> result = new Vector<>();

        try {
            this.socket = new Socket(ipAddress, portNum);
            socket.setSoTimeout(5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            out.println(message);

            Scanner in = new Scanner(socket.getInputStream());

            while(in.hasNextLine()) {
                result.add(in.nextLine());
            }

            //if(core.Main.development) {
            //    System.out.println("Client sent: " + message + " to: " + ipAddress + " responded: " + response);
            //}
            //core.Main.log.writeLogLine("Client sent: " + message + " to: " + ipAddress + " responded: " + response);

        }
        catch(java.net.SocketTimeoutException e)
        {
            result.add("FAILED TO AQUIRE LOGS FROM: " + ipAddress + " " + e.getMessage()) ;
            if(core.Main.development) {
                System.out.println("Socket Timeout Exception: " + e.getMessage());
            }

            //core.Main.log.writeLogLine("Socket Timeout Exception: " + e.getMessage());

        }
        catch (Exception e)
        {
            result.add("FAILED TO AQUIRE LOGS FROM: " + ipAddress + " " + e.getMessage()) ;
            if(core.Main.development) {
                System.out.println("Socket Exception: " + e.getMessage());
            }

            //core.Main.log.writeLogLine("Socket Exception: " + e.getMessage());
        }finally {
            try { socket.close(); } catch (IOException e) {

                if(core.Main.development) {
                    System.out.println("Socket Exception: " + e.getMessage());
                }

                //core.Main.log.writeLogLine("Socket Exception: " + e.getMessage());

            }

        }


        return result;

    }


}
