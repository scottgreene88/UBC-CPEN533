package commands;

import com.google.gson.*;
import core.GateWayManager;
import core.Main;
import data.UDPMessage;


public class UdpCommandManager implements Runnable {

    private String command;

    public UdpCommandManager(String command)
    {
        this.command = command;
    }

    public void run()
    {
        Gson json = new Gson();
        UDPMessage currentCommand = json.fromJson(command, UDPMessage.class);

        updateLocalClock(currentCommand.sendTimestamp);

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

    private void updateLocalClock(long commandClock)
    {

        if(commandClock > Main.localProcessClock.getClock()) {
            commandClock++;
            Main.localProcessClock.setClock(commandClock);
        }
        else{
            Main.localProcessClock.incrementClock();
        }

    }


    private void addNewMachineToGroup(UDPMessage currentCommand)
    {

        Main.currentMachineList.add(currentCommand.senderIP);
        Main.currentMachineListLoginTime.add(Main.localProcessClock.getClock());

        GateWayManager gateWayManager = new GateWayManager();
        gateWayManager.updatePredecessorsList();
        gateWayManager.updateSuccessorsList();

        updateAllMachines();
    }

    public static String removeMachineFromGroup()
    {
        //handle disconnect
        try {
            removeMachineFromCurrentList(Main.localHostIP);
        }catch (Exception e)
        {
            System.out.println("Inet error " + e.getMessage());
        }

        updateAllMachines();

        Main.processActive = false;

        return "Machine Disconnected";
    }

    public static void updateAllMachines()
    {
        try {
            //System.out.println("Notify all machines");
            GateWayManager gateWayManager = new GateWayManager();
            for (String ip : Main.currentMachineList) {
                if (!ip.equals(Main.localHostIP) )
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
            //System.out.println("Trying to remove ip: " + ip);
            int index = Main.currentMachineList.indexOf(ip);
            if(index != -1) {
                Main.currentMachineList.remove(index);
                Main.currentMachineListLoginTime.remove(index);
                Main.writeLog("Removed ip from current list: " + ip);

            }
        }catch(Exception e)
        {
            System.out.println("Exception when removing machine from list: " + e.getMessage());
        }


    }

    private void updateNeighbourLists(UDPMessage currentCommand)
    {

        GateWayManager gateWayManager = new GateWayManager();

        Main.masterIPAddress = currentCommand.masterNodeIP;

        Main.currentMachineList = gateWayManager.deserializeStringList(currentCommand.machineList);
        Main.currentMachineListLoginTime = gateWayManager.deserializeLongList(currentCommand.machineStartTimes);

        gateWayManager.updatePredecessorsList();
        gateWayManager.updateSuccessorsList();


    }
}
