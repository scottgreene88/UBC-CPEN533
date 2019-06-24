package commands;

import core.Main;

import data.ReadWriteManager;
import data.TCPMessage;
import network.fileTransferClient;
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
            case "nodeReady":
                sendFileToNode();
                break;
            case "sendFileToDestination":
                notifyNodeOfIncomingFile();
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
            TCPMessage localMessage = new TCPMessage("node", "checkAsk", Main.localHostIP, cmd.senderIP , Main.localProcessClock.getClock());

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
        //ReadWriteManager writer = new ReadWriteManager();
        //writer.writeFile(Main.fs533FileFolder +"/" + cmd.fs533FileName, Main.cacheFile);

        Main.localProcessClock.incrementClock();
        localMessage = new TCPMessage("node", "masterXferDone", Main.localHostIP, cmd.senderIP , Main.localProcessClock.getClock());

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);

        Main.localProcessClock.incrementClock();
        localMessage = new TCPMessage("master", "sendFileToDestination", Main.localHostIP, Main.localHostIP , Main.localProcessClock.getClock());

        Main.commandQueues.addCommandToInBoundQueue(localMessage);
    }

    private void notifyNodeOfIncomingFile()
    {
        Main.masterFileList.addNewFile(Main.cacheFileName);

        int counter = Main.masterFileList.getNodeCounter();

        int mainNode = counter % Main.currentMachineList.size() ;
        int backUp1 = (counter + 1) % Main.currentMachineList.size() ;
        int backUp2 = (counter + 2) % Main.currentMachineList.size() ;

        String mainIP = Main.currentMachineList.get(mainNode);
        TCPMessage localMessage;
        if(!mainIP.equals(Main.localHostIP)) {
            Main.localProcessClock.incrementClock();
            localMessage = new TCPMessage("node", "fileIncoming", Main.localHostIP, mainIP, Main.localProcessClock.getClock());
            localMessage.fs533FileName = Main.cacheFileName;
            localMessage.fileSaveType = "main";

            Main.commandQueues.addCommandToOutBoundQueue(localMessage);
        }else
        {
            System.out.println("Master Saving Locally Main");
            ReadWriteManager writer = new ReadWriteManager();
            writer.writeFile(Main.fs533FileFolder +"/" + Main.cacheFileName, Main.cacheFile);
            Main.localFileList.addFileToLocalList(Main.cacheFileName,"main");
        }

        String backupIP1 = Main.currentMachineList.get(backUp1);
        if(!backupIP1.equals(Main.localHostIP)) {
        Main.localProcessClock.incrementClock();
        localMessage = new TCPMessage("node", "fileIncoming", Main.localHostIP, backupIP1 , Main.localProcessClock.getClock());
        localMessage.fs533FileName = Main.cacheFileName;
        localMessage.fileSaveType = "backUp1";

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
        }else
        {
            System.out.println("Master Saving Locally Backup 1");
            ReadWriteManager writer = new ReadWriteManager();
            writer.writeFile(Main.fs533FileFolder +"/" + Main.cacheFileName, Main.cacheFile);
            Main.localFileList.addFileToLocalList(Main.cacheFileName,"backUp1");
        }

        String backupIP2 = Main.currentMachineList.get(backUp2);
        if(!backupIP2.equals(Main.localHostIP)) {
        Main.localProcessClock.incrementClock();
        localMessage = new TCPMessage("node", "fileIncoming", Main.localHostIP, backupIP2 , Main.localProcessClock.getClock());
        localMessage.fs533FileName = Main.cacheFileName;
        localMessage.fileSaveType = "backUp2";

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
        }else
        {
            System.out.println("Master Saving Locally Backup 2");
            ReadWriteManager writer = new ReadWriteManager();
            writer.writeFile(Main.fs533FileFolder +"/" + Main.cacheFileName, Main.cacheFile);
            Main.localFileList.addFileToLocalList(Main.cacheFileName,"backUp2");
        }
    }

    private void sendFileToNode()
    {

        //System.out.println("starting file Xfer master");
        ExecutorService fileXferOutThread = Executors.newSingleThreadExecutor();
        fileXferOutThread.execute(new fileTransferClient(cmd.senderIP));

        fileXferOutThread.shutdown();
        try {
            fileXferOutThread.awaitTermination(10000, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e)
        {
            fileXferOutThread.shutdownNow();
            System.out.println("File Transfer Thread Timed Out");
        }

        Main.cacheFileSaved = true;

        //Main.masterFileList.addIpToFileInfo(cmd.fs533FileName, Main.localHostIP);
    }

}
