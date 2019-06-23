package network;


import com.google.gson.Gson;
import core.Main;
import data.TCPMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TcpMessageServerManager implements Runnable {


    ServerSocket listener;

    public void run()
    {
        System.out.println("Starting TCP Server");
        Future<String> command;
        String commandReceived;



        try {
            listener = new ServerSocket(Main.inPortNum);
        }catch (IOException e)
        {
            System.out.println("Tcp Server Manager Scoket error: " + e.getMessage());
        }


        while(Main.processActive)
        {
            try {

                //ExecutorService es = Executors.newSingleThreadExecutor();
                ExecutorService es = Executors.newFixedThreadPool(6);
                command = es.submit(new TcpMessageServer(listener.accept()));

                commandReceived = command.get();

                Gson json = new Gson();

                TCPMessage jsonCommand = json.fromJson(commandReceived, TCPMessage.class);

                Main.localProcessClock.incrementClock();
                Main.commandQueues.addCommandToInBoundQueue(jsonCommand);


            }
            catch (Exception e)
            {
                try {
                    Main.writeLog("Exception in TCP Server Manager: " + e.getMessage());
                }
                catch (Exception e2)
                {
                    System.out.println("Log failure: " + e2.getMessage());
                }
            }
        }

    }
}
