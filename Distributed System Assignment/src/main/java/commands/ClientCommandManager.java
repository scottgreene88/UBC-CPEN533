package commands;

import core.Main;
import network.TcpMessageClient;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientCommandManager implements Runnable {


    private int portNum = Main.clientPortNum;
    private Socket socket;


    public void run()
    {
        try {
            ServerSocket listener = new ServerSocket(portNum);


        //set up the TCP port
        while(Main.processActive)
        {

            // listen for someone to connect
            socket  = listener.accept();
            Scanner inCommand = new Scanner(socket.getInputStream());
            PrintWriter outResponse = new PrintWriter(socket.getOutputStream(), true);
            //get user command
            String command = inCommand.nextLine();

            //execute the command
            Vector<String> response = executeCommand(command);

            //respond with the answer
            for(int i = 0; i < response.size(); i++)
            {
                outResponse.println(response.get(i));
            }
            socket.close();
        }
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

        for (int i = 0; i < Main.currentMachineList.size(); i++) {
            Future<Vector<String>> result = pool.submit(new TcpMessageClient(portNum, Main.currentMachineList.get(i), phrase));
            resultList.add(result);
        }

        for (int i = 0; i < resultList.size(); i++) {

            try {
                response.addAll(resultList.get(i).get());
            }catch (Exception e)
            {
                System.out.println("Exception getting future thread grep result: " + e.getMessage());
            }
        }


        return response;
    }

    private Vector<String> disconnectThisMachine()
    {
        Vector<String> response = new Vector<>();

        response.add(CommandManager.removeMachineFromGroup());

        return response ;

    }

    private Vector<String> findPhraseInLog(String phrase)
    {

        Vector<String> response = new Vector<>();
        try {

            String logFileName = "/home/ec2-user/Test/" + Main.logName;

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
