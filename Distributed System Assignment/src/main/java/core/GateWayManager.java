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

    private int numberOfNeightbours = 4;

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

            updatePredecessorsList();
            updateSuccessorsListList();

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
        Main.predecessorsList.clear();

        int selfIndex = 0;
        try {
            selfIndex = Main.currentMachineList.indexOf(InetAddress.getLocalHost().getHostAddress());
        }
        catch(Exception e)
        {
            System.out.println("InetAddress Exception" + e.getMessage());
        }

        if(Main.currentMachineList.size() == 1)
        {

        }
        else if(Main.currentMachineList.size() == 2)
        {
            if(selfIndex == 0)
            {
                Main.predecessorsList.add(Main.currentMachineList.get(1));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(1));
            }
            else {
                Main.predecessorsList.add(Main.currentMachineList.get(0));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(0));
            }

        }
        else if (Main.currentMachineList.size() == 3)
        {
            if(selfIndex == 0)
            {
                Main.predecessorsList.add(Main.currentMachineList.get(1));
                Main.predecessorsList.add(Main.currentMachineList.get(2));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(1));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(2));
            }
            else if(selfIndex == 1){
                Main.predecessorsList.add(Main.currentMachineList.get(0));
                Main.predecessorsList.add(Main.currentMachineList.get(2));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(0));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(2));
            }
            else
            {
                Main.predecessorsList.add(Main.currentMachineList.get(0));
                Main.predecessorsList.add(Main.currentMachineList.get(1));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(0));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(1));
            }
        }
        else if (Main.currentMachineList.size() == 4)
        {
            for(int i = 1; i <= numberOfNeightbours-1; i++)
            {

                if((selfIndex+ i) < Main.currentMachineList.size())
                {
                    Main.predecessorsList.add(Main.currentMachineList.get(selfIndex+i));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(selfIndex+i));
                }
                else
                {
                    int val =  (selfIndex + i) - Main.currentMachineList.size();
                    Main.predecessorsList.add(Main.currentMachineList.get(val));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(val));
                }

            }
        }
        else
        {

            for(int i = 1; i <= numberOfNeightbours; i++)
            {

                if((selfIndex+ i) < Main.currentMachineList.size())
                {
                    Main.predecessorsList.add(Main.currentMachineList.get(selfIndex+i));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(selfIndex+i));
                }
                else
                {
                    int val =  (selfIndex + i) - Main.currentMachineList.size();
                    Main.predecessorsList.add(Main.currentMachineList.get(val));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(val));
                }

            }

        }

        System.out.println("CURRENT PRED LIST:");
        for (String s: Main.predecessorsList
             ) {
            System.out.println(s);
        }
    }

    public void updateSuccessorsListList()
    {
        Main.successorsList.clear();

        int selfIndex = 0;
        try {
            selfIndex = Main.currentMachineList.indexOf(InetAddress.getLocalHost().getHostAddress());
        }
        catch(Exception e)
        {
            System.out.println("InetAddress Exception" + e.getMessage());
        }
        if(Main.currentMachineList.size() == 1)
        {

        }
        else if(Main.currentMachineList.size() == 2)
        {
            if(selfIndex == 0) {
                Main.successorsList.add(Main.currentMachineList.get(1));
                //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(1));
            }else {
                Main.successorsList.add(Main.currentMachineList.get(0));
                //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(0));
            }
        }
        else if (Main.currentMachineList.size() == 3)
        {
            if(selfIndex == 0)
            {
                Main.successorsList.add(Main.currentMachineList.get(1));
                //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(1));
                Main.successorsList.add(Main.currentMachineList.get(2));
                //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(2));
            }
            else if(selfIndex == 1){
                Main.successorsList.add(Main.currentMachineList.get(0));
                //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(0));
                Main.successorsList.add(Main.currentMachineList.get(2));
                //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(2));
            }
            else
            {
                Main.successorsList.add(Main.currentMachineList.get(0));
                //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(0));
                Main.successorsList.add(Main.currentMachineList.get(1));
                //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(1));
            }
        }
        else if (Main.currentMachineList.size() == 4)
        {
            for(int i = 1; i <= numberOfNeightbours - 1; i++)
            {

                if((selfIndex - i) >= 0)
                {

                    Main.successorsList.add(Main.currentMachineList.get(selfIndex-i));
                    //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(selfIndex-i));
                }
                else
                {
                    int val =  Main.currentMachineList.size() - i;

                    Main.successorsList.add(Main.currentMachineList.get(val));
                    //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(val));
                }

            }
        }
        else
        {
            for(int i = 1; i <= numberOfNeightbours; i++)
            {

                if((selfIndex - i) >= 0)
                {

                    Main.successorsList.add(Main.currentMachineList.get(selfIndex-i));
                    //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(selfIndex-i));
                }
                else
                {
                    int val =  Main.currentMachineList.size() - i;

                    Main.successorsList.add(Main.currentMachineList.get(val));
                    //Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(val));
                }

            }
        }

        System.out.println("CURRENT SUC LIST:");
        for (String s: Main.successorsList
        ) {
            System.out.println(s);
        }

    }

}
