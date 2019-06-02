package commands;


import java.net.*;

import network.TcpMessageServer;

public class CommandServer implements Runnable {

    private Socket socket;

    public CommandServer(Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        String command;
        TcpMessageServer server =  new TcpMessageServer(socket);
        command = server.call();

        executeCommand(command);
    }

    private void executeCommand(String command)
    {
        //case switch here to hand all command functions

        switch (command)
        {
            case "REORG":
                commandReorganizePartners();
                break;
            case "DISCONNECT":
                commandDisconnectFromGroup();
                break;
            case "WhoIsGateWay":
                commandRespondWithGateWayAddress();
                break;
            default:
                System.out.println("INVALID COMMAND");
        }
    }

    private void respondToCommand(String response)
    {

    }

    private void commandReorganizePartners()
    {

    }

    private void commandDisconnectFromGroup()
    {

    }

    private void commandRespondWithGateWayAddress()
    {

    }
}
