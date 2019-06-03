package core;


import data.Machine;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.*;

import network.TcpMessageClient;

public class MachineListManager {

    private String machineListPath = "machinelist.json";
    public ArrayList<Machine> localMachineList;

    public Machine findGatewayNode() throws Exception {

        Machine gateway = null;

        localMachineList = readMachineListFile();

        if(localMachineList.isEmpty())
        {
            return getThisMachine();
        }

        Gson gson = new Gson();


        for (int i = 0; i < localMachineList.size(); i++) {
            Machine tempMachine =  localMachineList.get(i);
            TcpMessageClient client = new TcpMessageClient(OLDMain.commandServerPort, tempMachine.ipAddress);
            String response  = client.sendSingleMessage("WhoIsGateWay");




            if(!response.equals("FAILED"))
            {
                gson = new Gson();
                return gson.fromJson(response, Machine.class);
            }
        }

        return gateway;
    }

    public Machine getThisMachine() throws Exception {
        InetAddress local = InetAddress.getLocalHost();
        Date date = new Date();

        Machine current = new Machine(local.getHostAddress(), date, true, OLDMain.isGatewayNode);

        return current;
    }

    private ArrayList<Machine> readMachineListFile() throws IOException
    {
        Gson gson = new Gson();

        Machine[] currentArray;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(machineListPath));
            currentArray = gson.fromJson(bufferedReader, Machine[].class);


            for (Machine var: currentArray) {
                localMachineList.add(var);
            }
            
        }catch (FileNotFoundException e)
        {
            File file = new File(machineListPath);
            file.createNewFile();

        }

        return localMachineList;
    }

    //Test fucntion
    public void createTestMachineList() throws Exception
    {
        localMachineList = new ArrayList<>();
        Date date = new Date();
        Machine machine = new Machine("172.31.23.204",date , true, true);
        Machine machine1 = new Machine("172.31.29.195", date, false, false);
        Machine machine2 = new Machine("172.31.17.62", date, false, false);
        Machine machine3 = new Machine("172.31.29.58", date, false, false);
        Machine machine4 = new Machine("172.31.30.52", date, false, false);

        localMachineList.add(machine);
        localMachineList.add(machine1);
        localMachineList.add(machine2);
        localMachineList.add(machine3);
        localMachineList.add(machine4);

        Gson gson = new Gson();
        String jsonArray = gson.toJson(localMachineList);
        FileWriter writer = new FileWriter("machinelist.json");
        writer.write(jsonArray);
        writer.close();
    }

}