package commands;

import com.google.gson.Gson;
import core.Main;
import data.TCPMessage;
import network.TcpMessageClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientCommandManager implements Runnable {

    private int portNum = Main.clientPortNum;

    private TCPMessage cmd;
    private String command;

    public ClientCommandManager(String command)
    {

        this.command = command;

    }


    public void run()
    {
        try {

            //get user command
            Gson json = new Gson();

            cmd =  json.fromJson(command, TCPMessage.class);



            //execute the command
            Vector<String> response = executeCommand(cmd.messageType);

            Main.localProcessClock.incrementClock();
            TCPMessage responseMessage =  new TCPMessage("response",cmd.commandType,Main.localHostIP,Main.localProcessClock.getClock());
            responseMessage.dataList = new core.GateWayManager().serializeList(response) ;

            Main.commandQueues.addCommandToOutBoundQueue(responseMessage);

        }catch (Exception e)
        {
            System.out.println("Client Manager Exception: " + e.getMessage());
        }

    }

    private Vector<String> executeCommand(String command)
    {
        Vector<String> responseList =  new Vector<>();
        String[] grepCommand = {"",""};

        if(command.contains("grep") ||command.contains("phrase"))
        {
            grepCommand = command.split("\\s+");
            command = grepCommand[0];

        }


        switch (command){

            case "list":
                responseList = getCurrentMachineList();
                break;
            case "grep":
                responseList = grepForPhrase(grepCommand[1]);
                break;
            case "phrase":
                responseList = findPhraseInLog(grepCommand[1]);
                break;
            case "disconnect":
                responseList = disconnectThisMachine();
                break;
            default:
                responseList.add("Invalid Command");
                break;


        }

        return responseList;

    }

    private Vector<String> getCurrentMachineList()
    {
        Vector<String> response = new Vector<>();

        for(int i = 0; i < Main.currentMachineList.size(); i++)
        {
            response.add(Main.currentMachineList.get(i)+ " - " + Main.currentMachineListLoginTime.get(i));
        }

        return response;

    }

    private Vector<String> grepForPhrase(String phrase)
    {
        //Send the TCP String "phrase XXXXXXX" to all other Server Client Managers
        Vector<String> response = new Vector<>();

        ExecutorService pool = Executors.newFixedThreadPool(Main.currentMachineList.size());

        List<Future<Vector<String>>> resultList = new ArrayList<>();

        Gson json = new Gson();

        for (int i = 0; i < Main.currentMachineList.size(); i++) {
            if(Main.currentMachineList.get(i) != Main.localHostIP) {

                Main.localProcessClock.incrementClock();
                TCPMessage phraseMessage =  new TCPMessage("command","phrase "+ phrase,Main.localHostIP, Main.localProcessClock.getClock());

                String message =  json.toJson(phraseMessage);

                Future<Vector<String>> result = pool.submit(new TcpMessageClient(portNum, Main.currentMachineList.get(i), message));
                resultList.add(result);
            }
        }

        for (int i = 0; i < resultList.size(); i++) {

            try {
                response.addAll(resultList.get(i).get());
            }catch (Exception e)
            {
                System.out.println("Exception getting future thread grep result: " + e.getMessage());
            }
        }
        //add local log
        response.addAll(findPhraseInLog(phrase));

        return response;
    }

    private Vector<String> disconnectThisMachine()
    {
        Vector<String> response = new Vector<>();

        response.add(UdpCommandManager.removeMachineFromGroup());

        return response ;

    }

    private Vector<String> findPhraseInLog(String phrase)
    {

        Vector<String> response = new Vector<>();
        try {

            String logFileName = Main.logName;
            //String logFileName = "B:\\School\\CPEN 533\\Assignments Repo\\UBC-CPEN533\\Distributed System Assignment\\" + Main.logName;

            File file = new File(logFileName);

            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                if (st.matches(phrase)) {
                    response.add(st);
                }
            }
        }catch (Exception e)
        {
            System.out.println("Exception in findPhraseIngLogs " + e.getMessage());
        }

        return response;
    }
}
