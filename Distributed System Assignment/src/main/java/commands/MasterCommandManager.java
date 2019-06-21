package commands;

import core.Main;
import data.ReadWriteManager;
import data.TCPMessage;
import network.fileTransferServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MasterCommandManager implements Runnable {

    private TCPMessage cmd;

    public MasterCommandManager(TCPMessage cmd)
    {
        this.cmd = cmd;
    }

    public void run()
    {
        executeCommand(cmd.commandType);
    }

    private void executeCommand(String commandType)
    {

        switch(commandType){

            case "put":
                getPutFromServer();
                break;
            case "confirmAsk":

                break;
            case "confirmGet":

                break;
            case "putReady":

                break;
            case "masterXferDone":

                break;






        }

    }


    private void getPutFromServer()
    {


        //TODO: need to check if there has been a similar request for this file name within the last 60 seconds then send confirm message
        boolean checkIfFileRequestWithinMinute = false;

        if(checkIfFileRequestWithinMinute)
        {
            Main.localProcessClock.incrementClock();
            TCPMessage localMessage = new TCPMessage("node", "checkConfirm", Main.localHostIP, cmd.senderIP , Main.localProcessClock.getClock());

            Main.commandQueues.addCommandToOutBoundQueue(localMessage);

            return;
        }

        Main.cacheFileName = cmd.fs533FileName;

        ExecutorService fileXferInThread = Executors.newSingleThreadExecutor();
        fileXferInThread.execute(new fileTransferServer());

        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("node", "putReady", Main.localHostIP, cmd.senderIP , Main.localProcessClock.getClock());

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);

        fileXferInThread.shutdown();
        try {
            fileXferInThread.awaitTermination(10000, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e)
        {
            fileXferInThread.shutdownNow();
            System.out.println("File Transfer Thread Timed Out");
        }

        //TODO: need to call next set of instructions to send file to end destination

        //here will temp just save the file to a folder for testing
        ReadWriteManager writer = new ReadWriteManager();
        writer.writeFile(Main.fs533FileFolder + cmd.fs533FileName, Main.cacheFile);

        Main.localProcessClock.incrementClock();
        localMessage = new TCPMessage("node", "masterXferDone", Main.localHostIP, cmd.senderIP , Main.localProcessClock.getClock());

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }
}
