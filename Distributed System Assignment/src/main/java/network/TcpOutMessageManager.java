package network;

import com.google.gson.Gson;
import core.Main;
import data.TCPMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class TcpOutMessageManager implements Runnable {

    public void run()
    {

        Gson json = new Gson();

        while(Main.processActive) {

            if(!Main.commandQueues.checkOutBoundEmpty()) {

                TCPMessage outMessage =  Main.commandQueues.getCommandFromOutBoundQueue();

                try {
                    Socket socket = new Socket(outMessage.senderIP, Main.clientPortNum);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    out.println(json.toJson(outMessage));

                    socket.close();

                }catch (IOException e){
                    System.out.println("Exception in TCP Out Message manager " + e.getMessage());
                }




            }
        }
    }


}
