package network;


import core.Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import commands.CommandManager;

public class UdpMessageServerManager implements Runnable {

    public void run()
    {
        System.out.println("Starting Server");
        Future<String> command;
        while(Main.processActive)
        {
            try {
                //ExecutorService es = Executors.newSingleThreadExecutor();
                ExecutorService es = Executors.newFixedThreadPool(5);
                command = es.submit(new UdpMessageServer(Main.heartBeatPort));

                //ExecutorService es2 = Executors.newSingleThreadExecutor();
                ExecutorService es2 = Executors.newFixedThreadPool(5);
                es2.execute(new CommandManager(command.get()));

            }
            catch (Exception e)
            {
                try {
                    Main.writeLog("Exception in UDP Server Manager: " + e.getMessage());
                }
                catch (Exception e2)
                {
                    System.out.println("Log failure: " + e2.getMessage());
                }
            }
        }

    }
}
