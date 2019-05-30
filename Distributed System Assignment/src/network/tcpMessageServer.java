package network;

import main.Main;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Callable;


public class tcpMessageServer implements Callable {

    private Socket socket;

    public tcpMessageServer(Socket socket){

        this.socket = socket;
    }

    public String call()
    {
        String command = "";
        try {


            String logText = "TCP Server connected: " + socket;

            if (Main.development) {
                System.out.println(logText);
            }

            Main.log.writeLogLine(logText);


            try {
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("ACK");

                command = in.nextLine();

                String errorText = "TCP Server command received: " + command;

                if (Main.development) {
                    System.out.println(errorText);
                }

                Main.log.writeLogLine(errorText);

            } catch (Exception e) {
                logText = "TCP Server Exception: " + e.getMessage();

                if (Main.development) {
                    System.out.println(logText);
                }

                Main.log.writeLogLine(logText);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }

                logText = "TCP Server closed: " + socket;

                if (Main.development) {
                    System.out.println(logText);
                }

                Main.log.writeLogLine(logText);
            }


        }
        catch (Exception e)
        {
            System.out.println("Generic Exception Caught in command tcpMessageServer : " + e.getMessage());
        }

        return command;
    }

}
