package commands;

import com.google.gson.*;
import core.GateWayManager;
import core.Main;
import data.UDPMessage;

import java.net.InetAddress;
import java.util.Date;

public class CommandManager implements Runnable {

    private String command;

    public CommandManager(String command)
    {
        this.command = command;
    }

    public void run()
    {
        Gson json = new Gson();
        UDPMessage currentCommand = json.fromJson(command, UDPMessage.class);

        switch (currentCommand.messageType)
        {
            case "HB":
                updateHeartBeatTable(currentCommand);
                break;
            case "LOGIN":
                addNewMachineToGroup(currentCommand);
                break;
            case "UPDATE":
                updateNeighbourLists(currentCommand);
                break;


        }

    }

    private void updateHeartBeatTable(UDPMessage currentCommand)
    {
        Main.heartBeatTable.updateNewTimeStamp(currentCommand.senderIP, currentCommand.sendTimestamp);
    }

    private void addNewMachineToGroup(UDPMessage currentCommand)
    {
        Date date = new Date();
        Main.currentMachineList.add(currentCommand.senderIP);
        Main.currentMachineListLoginTime.add(date.toString());

        GateWayManager gateWayManager = new GateWayManager();
        gateWayManager.updatePredecessorsList();
        gateWayManager.updateSuccessorsList();

        updateAllMachines();
    }

    private boolean removeMachineFromGroup()
    {
        //test


        return true;
    }

    public static void updateAllMachines()
    {
        try {
            System.out.println("Notify all machines");
            GateWayManager gateWayManager = new GateWayManager();
            for (String ip : Main.currentMachineList) {
                if (!ip.equals(InetAddress.getLocalHost().getHostAddress()) )
                    gateWayManager.sendCurrentMachineList(ip);
            }
        }catch (Exception e)
        {
            System.out.println("Inet failure");
        }

    }

    public static void removeMachineFromCurrentList(String ip)
    {
        try {
            System.out.println("Trying to remove ip: " + ip);
            int index = Main.currentMachineList.indexOf(ip);
            if(index != -1) {
                Main.currentMachineList.remove(index);
                Main.currentMachineListLoginTime.remove(index);
                System.out.println("removed ip: " + ip);
            }
        }catch(Exception e)
        {
            System.out.println("Exception when removing machine from list: " + e.getMessage());
        }


    }

    private void updateNeighbourLists(UDPMessage currentCommand)
    {

        GateWayManager gateWayManager = new GateWayManager();

        Main.currentMachineList = gateWayManager.deserializeList(currentCommand.machineList);
        Main.currentMachineListLoginTime = gateWayManager.deserializeList(currentCommand.machineStartTimes);

        gateWayManager.updatePredecessorsList();
        gateWayManager.updateSuccessorsList();


    }
}
