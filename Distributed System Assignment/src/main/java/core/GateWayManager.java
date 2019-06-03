package core;

import com.google.gson.Gson;
import data.HeartBeatTable;
import data.UDPMessage;
import network.UdpMessageClient;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Date;
import java.util.Vector;

public class GateWayManager {

    public void requestLogin(String ip)
    {
        try {
            Gson json = new Gson();
            Date date = new Date();
            UDPMessage loginMessage = new UDPMessage("LOGIN", Inet4Address.getLocalHost().getHostAddress(), date);
            UdpMessageClient client;
            String message = json.toJson(loginMessage);


            client = new UdpMessageClient(Main.heartBeatPort, InetAddress.getByName(ip));
            client.sendMessage(message);
        }catch (Exception e)
        {
            System.out.println("request login GW failure");
        }

    }

    public void sendCurrentMachineList(String ip)
    {
        try {
            Gson json = new Gson();
            Date date = new Date();
            UDPMessage updateMessage = new UDPMessage("UPDATE", Inet4Address.getLocalHost().getHostAddress(), date);
            updateMessage.machineList = serializeList(Main.currentMachineList);
            updateMessage.machineStartTimes = serializeList(Main.currentMachineListLoginTime);
            UdpMessageClient client;
            String message = json.toJson(updateMessage);


            client = new UdpMessageClient(Main.heartBeatPort, InetAddress.getByName(ip));
            client.sendMessage(message);
        }catch (Exception e)
        {
            System.out.println("send machine failure");
        }


    }

    public String serializeList(Vector<String> list)
    {
        Gson json = new Gson();
        String message = json.toJson(list);
        return message;

    }

    public Vector<String> deserializeList(String list)
    {
        Gson json = new Gson();
        String[] temp = json.fromJson(list,String[].class);

        Vector<String> tempList = new Vector<>();

        for(int i = 0; i < temp.length; i++)
        {
            tempList.add(temp[i]);
        }

        return tempList;
    }

    public void updatePredecessorsList()
    {
        if(Main.currentMachineList.size() == 1)
        {

        }
        else if(Main.currentMachineList.size() == 2)
        {
            Main.predecessorsList.add(Main.currentMachineList.get(0));

        }
        else if (Main.currentMachineList.size() == 3)
        {

        }
        else
        {

        }
    }

    public void updateSuccessorsListList()
    {
        if(Main.currentMachineList.size() == 1)
        {

        }
        else if(Main.currentMachineList.size() == 2)
        {
            Main.successorsList.add(Main.currentMachineList.get(0));
            Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(0));
        }
        else if (Main.currentMachineList.size() == 3)
        {

        }
        else
        {

        }
    }

}
