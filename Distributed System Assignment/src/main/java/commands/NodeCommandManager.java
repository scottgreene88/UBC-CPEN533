package commands;

import core.Main;
import data.TCPMessage;
import network.TcpOutMessageManager;
import network.fileTransferClient;
import network.fileTransferServer;
import data.ReadWriteManager;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            case "fileIncoming":
                respondAndOpenPort();
                break;
            case "lsToMaster":
                sendLsToMaster();
                break;
            case "fullList":
                sendLsToClient();
                break;
            case "locateToMaster":
                sendLocateToMaster();
                break;
            case "locateList":
                sendLocateToClient();
                break;
            case "deleteFile":
                deleteLocalFIle();
                break;
            case "removeToMaster":
                sendRemoveToMaster();
                break;
            case "getToMaster":
                sendGetToMaster();
                break;
            case "get":
                sendGetFileToMaster();
                break;
            case "getReady":
                getFileFromMaster();
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
        TCPMessage localMessage = new TCPMessage("client", "masterXferDone", Main.localHostIP, Main.localHostIP , Main.localProcessClock.getClock());

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);

    }

    private void respondAndOpenPort()
    {
        Main.cacheFileName = cmd.fs533FileName;

        ExecutorService fileXferInThread = Executors.newSingleThreadExecutor();
        fileXferInThread.execute(new fileTransferServer());

        //System.out.println("File Xfer Socket Open");

        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("master", "nodeReady", Main.localHostIP, Main.masterIPAddress , Main.localProcessClock.getClock());
        localMessage.fs533FileName = Main.cacheFileName;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);

        fileXferInThread.shutdown();
        try {
            fileXferInThread.awaitTermination(10000, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e)
        {
            fileXferInThread.shutdownNow();
            System.out.println("File Transfer Thread Timed Out");
        }

        Main.localFileList.addFileToLocalList(Main.cacheFileName, cmd.fileSaveType);

        ReadWriteManager writer = new ReadWriteManager();
        writer.writeFile(Main.fs533FileFolder +"/" + Main.cacheFileName, Main.cacheFile);

        Main.cacheFileSaved = true;
    }

    private void sendLsToMaster()
    {

        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("master", "lsFromNode", Main.localHostIP, Main.masterIPAddress , Main.localProcessClock.getClock());

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }

    private void sendLsToClient()
    {

        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("client", "lsResponse", Main.localHostIP, Main.localHostIP , Main.localProcessClock.getClock());
        localMessage.dataList = cmd.dataList;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);

    }

    private void sendLocateToMaster()
    {
        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("master", "locateAtMaster", Main.localHostIP, Main.masterIPAddress , Main.localProcessClock.getClock());
        localMessage.fs533FileName = cmd.fs533FileName;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }

    private void sendLocateToClient()
    {

        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("client", "locateResponse", Main.localHostIP, Main.localHostIP , Main.localProcessClock.getClock());
        localMessage.dataList = cmd.dataList;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }

    private void sendRemoveToMaster()
    {
        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("master", "removeAtMaster", Main.localHostIP, Main.masterIPAddress , Main.localProcessClock.getClock());
        localMessage.fs533FileName = cmd.fs533FileName;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);

    }

    private void deleteLocalFIle()
    {
        System.out.println("Deleting Local File " +  cmd.fs533FileName);
        File file = new File(Main.fs533FileFolder + "/" + cmd.fs533FileName);
        file.delete();

        Main.localFileList.removeFileFromList(cmd.fs533FileName,cmd.fileSaveType);


    }

    private void sendGetToMaster()
    {
        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("master", "getAtMaster", Main.localHostIP, Main.masterIPAddress , Main.localProcessClock.getClock());
        localMessage.fs533FileName = cmd.fs533FileName;
        localMessage.localFileName = cmd.localFileName;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }

    private void sendGetFileToMaster()
    {
        ReadWriteManager reader = new ReadWriteManager();
        reader.cacheFile(Main.fs533FileFolder + "/" + cmd.fs533FileName);
        Main.cacheFileName = cmd.fs533FileName;

        System.out.println("Sending file to master " + Main.cacheFileName);

        ExecutorService fileXferOutThread = Executors.newSingleThreadExecutor();
        fileXferOutThread.execute(new fileTransferClient(Main.masterIPAddress));

        fileXferOutThread.shutdown();
        try {
            fileXferOutThread.awaitTermination(10000, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e)
        {
            fileXferOutThread.shutdownNow();
            System.out.println("File Transfer Thread Timed Out");
        }
    }

    private void getFileFromMaster()
    {

        ExecutorService fileXferInThread = Executors.newSingleThreadExecutor();
        fileXferInThread.execute(new fileTransferServer());

        Main.localProcessClock.incrementClock();
        TCPMessage localMessage = new TCPMessage("master", "nodeReady", Main.localHostIP, Main.masterIPAddress , Main.localProcessClock.getClock());
        localMessage.fs533FileName = cmd.fs533FileName;
        localMessage.localFileName = cmd.localFileName;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);

        fileXferInThread.shutdown();
        try {
            fileXferInThread.awaitTermination(10000, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e)
        {
            fileXferInThread.shutdownNow();
            System.out.println("File Transfer Thread Timed Out");
        }

        System.out.println("Received File From Master" + cmd.fs533FileName);
        Main.cacheFileName = cmd.fs533FileName;

        ReadWriteManager writer = new ReadWriteManager();
        writer.writeFile(cmd.localFileName, Main.cacheFile);

    }
}
