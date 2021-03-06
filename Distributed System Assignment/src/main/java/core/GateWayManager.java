package core;

import com.google.gson.Gson;
import data.UDPMessage;
import network.UdpMessageClient;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.*;

public class GateWayManager {

    private int numberOfNeighbours = 4;

    public void requestLogin(String ip)
    {
        try {
            Gson json = new Gson();
            Main.localProcessClock.incrementClock();
            UDPMessage loginMessage = new UDPMessage("LOGIN", Main.localHostIP, Main.localProcessClock.getClock());
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
            Main.localProcessClock.incrementClock();
            UDPMessage updateMessage = new UDPMessage("UPDATE", Main.localHostIP, Main.localProcessClock.getClock());
            updateMessage.machineList = serializeList(Main.currentMachineList);
            updateMessage.machineStartTimes = serializeList(Main.currentMachineListLoginTime);
            updateMessage.masterNodeIP = Main.masterIPAddress;
            UdpMessageClient client;
            String message = json.toJson(updateMessage);

            client = new UdpMessageClient(Main.heartBeatPort, InetAddress.getByName(ip));
            client.sendMessage(message);
        }catch (Exception e)
        {
            System.out.println("send machine failure");
        }


    }

    public String serializeList(Vector<?> list)
    {
        Gson json = new Gson();
        String message = json.toJson(list);
        return message;

    }

    public Vector<String> deserializeStringList(String list)
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


    public Vector<Long> deserializeLongList(String list) {
        Gson json = new Gson();
        Long[] temp = json.fromJson(list, Long[].class);
        Vector<Long> tempList = new Vector<>();
        for (int i = 0; i < temp.length; i++) {
            tempList.add(temp[i]);
        }
        return tempList;
    }

    synchronized public void updatePredecessorsList()
    {

        //System.out.println("Updating Pred list");

        int selfIndex = 0;
        try {

            while(Main.listBusyLock)
            {
                //System.out.println("Waiting for lock");
                //if lists are being updated then wait for them to finish
                Thread.sleep(50);
            }

            Main.listBusyLock = true;

            Main.predecessorsList.clear();
            Main.heartBeatTable.clearLists();

            selfIndex = Main.currentMachineList.indexOf(Main.localHostIP);
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
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(1), Main.currentMachineListLoginTime.get(1));
            }
            else {
                Main.predecessorsList.add(Main.currentMachineList.get(0));
                Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(0), Main.currentMachineListLoginTime.get(0));
            }

        }
        else if (Main.currentMachineList.size() == 3)
        {
            for(int i = 1; i <= numberOfNeighbours - 2; i++)
            {

                if((selfIndex - i) >= 0)
                {
                    Main.predecessorsList.add(Main.currentMachineList.get(selfIndex - i));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(selfIndex - i), Main.currentMachineListLoginTime.get(selfIndex - i));

                }
                else
                {
                    int val =  (selfIndex - i) + Main.currentMachineList.size();
                    Main.predecessorsList.add(Main.currentMachineList.get(val));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(val), Main.currentMachineListLoginTime.get(val));

                }

            }
        }
        else if (Main.currentMachineList.size() == 4)
        {

            for(int i = 1; i <= numberOfNeighbours - 1; i++)
            {

                if((selfIndex - i) >= 0)
                {
                    Main.predecessorsList.add(Main.currentMachineList.get(selfIndex - i));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(selfIndex - i), Main.currentMachineListLoginTime.get(selfIndex - i));

                }
                else
                {
                    int val =  (selfIndex - i) + Main.currentMachineList.size();
                    Main.predecessorsList.add(Main.currentMachineList.get(val));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(val), Main.currentMachineListLoginTime.get(val));

                }

            }
        }
        else
        {

            for(int i = 1; i <= numberOfNeighbours; i++)
            {

                if((selfIndex - i) >= 0)
                {
                    Main.predecessorsList.add(Main.currentMachineList.get(selfIndex - i));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(selfIndex - i), Main.currentMachineListLoginTime.get(selfIndex - i));

                }
                else
                {
                    int val =  (selfIndex - i) + Main.currentMachineList.size();
                    Main.predecessorsList.add(Main.currentMachineList.get(val));
                    Main.heartBeatTable.addPredecessor(Main.currentMachineList.get(val), Main.currentMachineListLoginTime.get(val));

                }

            }

        }

        Main.listBusyLock = false;
        //System.out.println("CURRENT PRED LIST:");
        //for (String s: Main.predecessorsList
        //     ) {
        //    System.out.println(s);
        //}
    }

/*
    synchronized public void updateSuccessorsList()
    {
        Main.successorsList.clear();

        int selfIndex = 0;
        try {
            selfIndex = Main.currentMachineList.indexOf(Main.localHostIP);
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
            for(int i = 1; i <= numberOfNeighbours - 2; i++)
            {

                if((selfIndex+ i) < Main.currentMachineList.size())
                {

                    Main.successorsList.add(Main.currentMachineList.get(selfIndex + i));
                }
                else
                {
                    int val =  (selfIndex + i) - Main.currentMachineList.size();


                    Main.successorsList.add(Main.currentMachineList.get(val));
                }

            }
        }
        else if (Main.currentMachineList.size() == 4)
        {
            for(int i = 1; i <= numberOfNeighbours - 1; i++)
            {

                if((selfIndex+ i) < Main.currentMachineList.size())
                {

                    Main.successorsList.add(Main.currentMachineList.get(selfIndex + i));
                }
                else
                {
                    int val =  (selfIndex + i) - Main.currentMachineList.size();


                    Main.successorsList.add(Main.currentMachineList.get(val));
                }

            }

        }
        else
        {
            for(int i = 1; i <= numberOfNeighbours; i++)
            {

                if((selfIndex+ i) < Main.currentMachineList.size())
                {

                    Main.successorsList.add(Main.currentMachineList.get(selfIndex + i));
                }
                else
                {
                    int val =  (selfIndex + i) - Main.currentMachineList.size();


                    Main.successorsList.add(Main.currentMachineList.get(val));
                }

            }
        }

        //System.out.println("CURRENT SUC LIST:");
        //for (String s: Main.successorsList
        //) {
        //   System.out.println(s);
        //}

    }
*/
    synchronized public void updateSuccessorsList()
    {

        //System.out.println("Updating Suc list");

        int selfIndex = 0;
        try {

            while(Main.listBusyLock)
            {
                System.out.println("Waiting for lock");
                //if lists are being updated then wait for them to finish
                Thread.sleep(50);
            }

            Main.listBusyLock = true;

            Main.successorsList.clear();

            selfIndex = Main.currentMachineList.indexOf(Main.localHostIP);
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
            for(int i = 1; i <= numberOfNeighbours - 2; i++)
            {

                if((selfIndex + i) < Main.currentMachineList.size())
                {

                    Main.successorsList.add(Main.currentMachineList.get(selfIndex + i));
                }
                else
                {
                    int val =  (selfIndex + i) - Main.currentMachineList.size();


                    Main.successorsList.add(Main.currentMachineList.get(val));
                }

            }
        }
        else if (Main.currentMachineList.size() == 4)
        {
            for(int i = 1; i <= numberOfNeighbours - 1; i++)
            {

                if((selfIndex+ i) < Main.currentMachineList.size())
                {

                    Main.successorsList.add(Main.currentMachineList.get(selfIndex + i));
                }
                else
                {
                    int val =  (selfIndex + i) - Main.currentMachineList.size();


                    Main.successorsList.add(Main.currentMachineList.get(val));
                }

            }

        }
        else
        {
            for(int i = 1; i <= numberOfNeighbours; i++)
            {

                if((selfIndex+ i) < Main.currentMachineList.size())
                {

                    Main.successorsList.add(Main.currentMachineList.get(selfIndex + i));
                }
                else
                {
                    int val =  (selfIndex + i) - Main.currentMachineList.size();


                    Main.successorsList.add(Main.currentMachineList.get(val));
                }

            }
        }

        //System.out.println("CURRENT SUC LIST:");
        //for (String s: Main.successorsList
        //) {
        //   System.out.println(s);
        //}


        Main.listBusyLock = false;

    }


}
