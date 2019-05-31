package commands;


import java.net.*;
import network.tcpMessageServer;

public class commandServer implements Runnable {

    private Socket socket;

    public commandServer(Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        String command;
        tcpMessageServer server =  new tcpMessageServer(socket);
        command = server.call();

        executeCommand(command);
    }

    private void executeCommand(String command)
    {
        //case switch here to hand all command functions
    }
}
