package commands;

import core.Main;
import data.TCPMessage;
import network.TcpOutMessageManager;
import network.fileTransferClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeCommandManager implements Runnable {

    private TCPMessage cmd;

    public NodeCommandManager(TCPMessage cmd)
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
                sendPutMessageToMaster();
                break;
            case "confirmAsk":
                sendConfirmToClient();
                break;
            case "confirmGet":
                forwardConfirmToMaster();
                break;
            case "putReady":
                startFileXferToMaster();
                break;
            case "masterXferDone":
                sendFileSavedConfirmToClient();
                break;






        }

    }

    private void sendPutMessageToMaster()
    {
        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("master", "put", Main.localHostIP, Main.masterIPAddress , Main.localProcessClock.getClock());
        localMessage.fs533FileName = cmd.fs533FileName;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }

    private void sendConfirmToClient()
    {
        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("client", "confirmAsk", Main.localHostIP, Main.localHostIP , Main.localProcessClock.getClock());
        localMessage.fs533FileName = cmd.fs533FileName;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }
    private void forwardConfirmToMaster()
    {
        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("master", "confirmGet", Main.localHostIP, Main.masterIPAddress , Main.localProcessClock.getClock());
        localMessage.fileSaveConfirm = cmd.fileSaveConfirm;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);

    }

    private void startFileXferToMaster()
    {

        ExecutorService fileXferOutThread = Executors.newSingleThreadExecutor();
        fileXferOutThread.execute(new fileTransferClient(Main.masterIPAddress));

    }

    private void sendFileSavedConfirmToClient()
    {
        Main.cacheFileSaved = true;
        Main.localProcessClock.incrementClock();
        //TCPMessage localMessage = new TCPMessage("client", "masterXferDone", Main.localHostIP, Main.localHostIP , Main.localProcessClock.getClock());

        //Main.commandQueues.addCommandToOutBoundQueue(localMessage);

    }
}
