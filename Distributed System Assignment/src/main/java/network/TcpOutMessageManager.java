package network;

import com.google.gson.Gson;
import core.Main;
import data.TCPMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpOutMessageManager implements Runnable {

    public void run()
    {

        Gson json = new Gson();

        while(Main.processActive) {

            if(!Main.commandQueues.checkOutBoundEmpty()) {

                TCPMessage outMessage =  Main.commandQueues.getCommandFromOutBoundQueue();

                try {


                    Socket  socket = new Socket(outMessage.destinationIP, Main.inPortNum);

                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    Main.localProcessClock.incrementClock();


                    String jsonMessage = json.toJson(outMessage);

                    Main.writeLog("TCP Out Client Send: " + jsonMessage);

                    out.println(jsonMessage);

                    socket.close();

                    Thread.sleep(50);

                }catch (Exception e){
                    System.out.println("Exception in TCP Out Message manager " + e.getMessage());
                }




            }
        }
    }


}
