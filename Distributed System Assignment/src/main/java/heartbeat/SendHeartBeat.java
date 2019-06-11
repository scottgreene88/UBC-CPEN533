package heartbeat;

import core.Main;
import data.UDPMessage;
import network.UdpMessageClient;
import java.net.*;
import java.util.Date;

import com.google.gson.*;

public class SendHeartBeat implements Runnable {

    public void run()
    {
        try {
            if(Main.processActive) {
                Gson json = new Gson();
                Date date = new Date();
                UDPMessage heartBeatMessage = new UDPMessage("HB", Main.localHostIP, date);
                UdpMessageClient client;
                String message = json.toJson(heartBeatMessage);

                for (String ip : Main.successorsList) {

                    client = new UdpMessageClient(Main.heartBeatPort, InetAddress.getByName(ip));
                    client.sendMessage(message);

                }
            }
        }catch(Exception e)
        {
            System.out.println("Exception in OLDSendHeartBeat: " + e.getMessage());
        }
    }
}


