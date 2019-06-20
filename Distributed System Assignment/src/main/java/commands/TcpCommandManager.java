package commands;


import core.Main;
import data.TCPMessage;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpCommandManager implements Runnable {

    public void run()
    {

        while(Main.processActive)
        {

            if(!Main.commandQueues.checkInBoundEmpty()) {

                TCPMessage inputMessage = Main.commandQueues.getCommandFromInBoundQueue();
                Main.localProcessClock.incrementClock();

                //if the sender is this machine then it is a client request
                if(inputMessage.messageType.equals("client") )
                {
                    ExecutorService clientThread = Executors.newSingleThreadExecutor();
                    clientThread.execute(new ClientCommandManager(inputMessage));
                }
                else if(inputMessage.messageType.equals("node"))
                {
                    ExecutorService nodeThread = Executors.newSingleThreadExecutor();
                    nodeThread.execute(new NodeCommandManager(inputMessage));
                }
                else if(inputMessage.messageType.equals("master"))
                {
                    ExecutorService masterThread = Executors.newSingleThreadExecutor();
                    masterThread.execute(new MasterCommandManager(inputMessage));
                }

            }

        }
    }

}
