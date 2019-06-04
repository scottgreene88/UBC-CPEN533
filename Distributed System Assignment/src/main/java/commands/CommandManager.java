package commands;

import com.google.gson.*;
import core.GateWayManager;
import core.Main;
import data.UDPMessage;

import java.net.InetAddress;
import java.util.Date;
import java.util.Vector;

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
            case "DISCONNECT":
                removeMachineFromGroup(currentCommand);
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

        gateWayManager.sendCurrentMachineList(currentCommand.senderIP);
        updateAllMachines();
    }

    private void removeMachineFromGroup(UDPMessage currentCommand)
    {

    }

    private void updateAllMachines()
    {
        GateWayManager gateWayManager = new GateWayManager();
        for (String ip: Main.currentMachineList) {
            gateWayManager.sendCurrentMachineList(ip);
        }

    }


    private void updateNeighbourLists(UDPMessage currentCommand)
    {

        GateWayManager gateWayManager = new GateWayManager();

        Main.currentMachineList = gateWayManager.deserializeList(currentCommand.machineList);
        Main.currentMachineListLoginTime = gateWayManager.deserializeList(currentCommand.machineStartTimes);

        gateWayManager.updatePredecessorsList();
        gateWayManager.updateSuccessorsListList();


    }
}
