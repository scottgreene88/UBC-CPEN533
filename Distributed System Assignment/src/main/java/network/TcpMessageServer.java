package network;

import core.OLDMain;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Callable;


public class TcpMessageServer implements Callable {

    private Socket socket;

    public TcpMessageServer(Socket socket){

        this.socket = socket;
    }

    public String call()
    {
        String command = "";
        try {


            String logText = "TCP Server connected: " + socket;

            if (OLDMain.development) {
                System.out.println(logText);
            }

            OLDMain.log.writeLogLine(logText);


            try {
                Scanner in = new Scanner(socket.getInputStream());
                //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //out.println("ACK");

                command = in.nextLine();

                String errorText = "TCP Server command received: " + command;

                if (OLDMain.development) {
                    System.out.println(errorText);
                }

                OLDMain.log.writeLogLine(errorText);

            } catch (Exception e) {
                logText = "TCP Server Exception: " + e.getMessage();

                if (OLDMain.development) {
                    System.out.println(logText);
                }

                OLDMain.log.writeLogLine(logText);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }

                logText = "TCP Server closed: " + socket;

                if (OLDMain.development) {
                    System.out.println(logText);
                }

                OLDMain.log.writeLogLine(logText);
            }


        }
        catch (Exception e)
        {
            System.out.println("Generic Exception Caught in command TcpMessageServer : " + e.getMessage());
        }

        return command;
    }

}
