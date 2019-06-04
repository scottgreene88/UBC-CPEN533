package network;

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

            if (core.Main.development) {
                System.out.println(logText);
            }

            core.Main.log.writeLogLine(logText);


            try {
                Scanner in = new Scanner(socket.getInputStream());
                //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //out.println("ACK");

                command = in.nextLine();

                String errorText = "TCP Server command received: " + command;

                if (core.Main.development) {
                    System.out.println(errorText);
                }

                core.Main.log.writeLogLine(errorText);

            } catch (Exception e) {
                logText = "TCP Server Exception: " + e.getMessage();

                if (core.Main.development) {
                    System.out.println(logText);
                }

                core.Main.log.writeLogLine(logText);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }

                logText = "TCP Server closed: " + socket;

                if (core.Main.development) {
                    System.out.println(logText);
                }

                core.Main.log.writeLogLine(logText);
            }


        }
        catch (Exception e)
        {
            System.out.println("Generic Exception Caught in command TcpMessageServer : " + e.getMessage());
        }

        return command;
    }

}
