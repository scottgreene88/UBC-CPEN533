package network;

import core.Main;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class fileTransferServer implements Runnable{

    public void run()
    {
        try {
            ArrayList<String> incomingFile =  new ArrayList<>();

            ServerSocket listener = new ServerSocket(Main.fileTransferPortNUm);

            Socket socket =  listener.accept();

            Scanner in = new Scanner(socket.getInputStream());

            while(in.hasNextLine())
            {
                incomingFile.add(in.nextLine());
            }

            Main.cacheFile.clear();
            Main.cacheFile.addAll(incomingFile);

            Main.cacheFileSaved = false;

            socket.close();
            listener.close();

        }catch (Exception e)
        {
            System.out.println("File Transfer Server Error: " + e.getMessage());
        }


    }

}
