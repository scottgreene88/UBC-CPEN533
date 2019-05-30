package main;

import network.tcpMessageServer;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class commandServerManager implements Runnable {

    public int portNum;

    public commandServerManager(int portNum)
    {
        this.portNum =  portNum;
    }

    public void run()
    {
        try {
            try {
                ServerSocket listener = new ServerSocket(portNum);
                System.out.println("The command server is running.");
                ExecutorService pool = Executors.newFixedThreadPool(20);
                while (true) {
                    pool.execute(new commandServer(listener.accept()));
                }
            } catch (Exception e) {
                if (Main.development) {
                    System.out.println("Exception from hbManager: " + e.getMessage());
                }
                Main.log.writeLogLine("Exception hbManager: " + e.getMessage());
            }
        }
        catch (Exception e)
        {
            System.out.println("Generic Exception Caught in command Server Manager: " + e.getMessage());
        }


    }

}
