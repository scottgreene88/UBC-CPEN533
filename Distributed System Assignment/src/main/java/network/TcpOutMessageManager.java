package network;

import core.Main;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class TcpOutMessageManager implements Runnable {


    public Queue<String> outboundCommandQueue;


    public void run()
    {
        outboundCommandQueue = new LinkedList<>();

        while(Main.processActive) {

            if(!outboundCommandQueue.isEmpty()) {

                //need to make the inbound and outbound message queues to work
                //client command manager needs to put its response onto the outbound queue

            }
        }
    }


}
