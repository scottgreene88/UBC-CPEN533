package commands;

import core.Main;

import java.io.IOException;
import java.net.*;

public class ClientCommandManager implements Runnable {


    private int portNum = Main.clientPortNum;
    private Socket socket;


    public void run()
    {
        try {
            ServerSocket listener = new ServerSocket(portNum);

        //set up the TCP port
        while(Main.processActive)
        {

            // listen for someone to connect
            listener.accept();

            //execute the command

            //respond with the answer


        }
        }catch (Exception e)
        {
            System.out.println("Client Manager Exception: " + e.getMessage());
        }

    }






}
