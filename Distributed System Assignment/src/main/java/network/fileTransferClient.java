package network;

import core.Main;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class fileTransferClient implements Runnable {

    private String destinationIP;

    public fileTransferClient(String destinationIP)
    {
        this.destinationIP = destinationIP;
    }

    public void run()
    {
        try {
            Socket socket = new Socket(destinationIP, Main.fileTransferPortNUm);

            socket.setSoTimeout(5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            ArrayList<String> currentFile = new ArrayList<>(Main.cacheFile);

            for (String line: currentFile) {

                out.println(line);
            }

            socket.close();

        }catch (Exception e)
        {
            System.out.println("File Transfer Client Error: " + e.getMessage());
        }

    }

}
