package data;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.Queue;

public class CommandQueues {

    private Queue<TCPMessage> outboundCommandQueue;
    private Queue<TCPMessage> inboundCommandQueue;

    public CommandQueues()
    {
        outboundCommandQueue = new LinkedList<>();
        inboundCommandQueue = new LinkedList<>();
    }

    public synchronized void addCommandToOutBoundQueue(TCPMessage message)
    {
        outboundCommandQueue.add(message);

    }

    public synchronized TCPMessage getCommandFromOutBoundQueue()
    {
        return outboundCommandQueue.remove();
    }

    public synchronized boolean checkOutBoundEmpty()
    {
        return outboundCommandQueue.isEmpty();
    }

    public synchronized boolean checkInBoundEmpty()
    {
        return inboundCommandQueue.isEmpty();
    }

    public synchronized TCPMessage getCommandFromInBoundQueue()
    {
        return inboundCommandQueue.remove();
    }

    public synchronized void addCommandToInBoundQueue(TCPMessage inputCommand)
    {
        inboundCommandQueue.add(inputCommand);
    }

}
