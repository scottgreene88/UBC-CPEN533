package commands;

import com.google.gson.Gson;
import core.Main;

import data.FileInfo;
import data.ReadWriteManager;
import data.TCPMessage;
import network.fileTransferClient;
import network.fileTransferServer;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.File;

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
            case "lsFromNode":
                sendFullListToNode();
                break;
            case "locateAtMaster":
                sendLocateToNode();
                break;
            case "removeAtMaster":
                sendRemoveFileToNodes();
                break;
            case "getAtMaster":
                sendGetToNode();
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

        FileInfo newFile = new FileInfo(Main.cacheFileName);

        int counter = Main.masterFileList.getNodeCounter();

        int mainNode = counter % Main.currentMachineList.size() ;
        int backUp1 = (counter + 1) % Main.currentMachineList.size() ;
        int backUp2 = (counter + 2) % Main.currentMachineList.size() ;

        String mainIP = Main.currentMachineList.get(mainNode);
        newFile.mainIP = mainIP;

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
        newFile.backupIP1 = backupIP1;

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
        newFile.backupIP2 = backupIP2;

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

        Main.masterFileList.addNewFile(newFile);
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


    }

    private void sendFullListToNode()
    {
        Vector<String> responseList = new Vector<>(Main.masterFileList.getMasterIndex());

        Gson json = new Gson();

        TCPMessage localMessage = new TCPMessage("node", "fullList", Main.localHostIP, cmd.senderIP, Main.localProcessClock.getClock());
        localMessage.dataList = json.toJson(responseList);

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }

    private void sendLocateToNode()
    {

        int index = Main.masterFileList.getIndexOfFile(cmd.fs533FileName);

        FileInfo temp = Main.masterFileList.getFileFromIndex(index);

        Gson json = new Gson();

        Vector<String> responseList = new Vector<>();
        responseList.add(temp.mainIP);
        responseList.add(temp.backupIP1);
        responseList.add(temp.backupIP2);

        TCPMessage localMessage = new TCPMessage("node", "locateList", Main.localHostIP, cmd.senderIP, Main.localProcessClock.getClock());
        localMessage.dataList = json.toJson(responseList);

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);
    }

    private void sendRemoveFileToNodes()
    {
        int index = Main.masterFileList.getIndexOfFile(cmd.fs533FileName);

        FileInfo temp = Main.masterFileList.getFileFromIndex(index);


        TCPMessage localMessage;

        Main.masterFileList.removeFileFromList(index);

        String mainIP = temp.mainIP;
        if(!mainIP.equals(Main.localHostIP)) {
            Main.localProcessClock.incrementClock();
            localMessage = new TCPMessage("node", "deleteFile", Main.localHostIP, mainIP, Main.localProcessClock.getClock());
            localMessage.fs533FileName = cmd.fs533FileName;
            localMessage.fileSaveType = "main";

            Main.commandQueues.addCommandToOutBoundQueue(localMessage);
        }else
        {
            System.out.println("Deleting File Locally "+  cmd.fs533FileName);

            File file = new File(Main.fs533FileFolder + "/" + cmd.fs533FileName);
            file.delete();

            Main.localFileList.removeFileFromList(cmd.fs533FileName,"main");
        }

        String backUpIP1 = temp.backupIP1;
        if(!backUpIP1.equals(Main.localHostIP)) {
            Main.localProcessClock.incrementClock();
            localMessage = new TCPMessage("node", "deleteFile", Main.localHostIP, backUpIP1, Main.localProcessClock.getClock());
            localMessage.fs533FileName = cmd.fs533FileName;
            localMessage.fileSaveType = "backUp1";

            Main.commandQueues.addCommandToOutBoundQueue(localMessage);
        }else
        {
            System.out.println("Deleting File Locally "+  cmd.fs533FileName);

            File file = new File(Main.fs533FileFolder + "/" + cmd.fs533FileName);
            file.delete();

            Main.localFileList.removeFileFromList(cmd.fs533FileName,"backUp1");
        }

        String backUpIP2 = temp.backupIP2;
        if(!backUpIP2.equals(Main.localHostIP)) {
            Main.localProcessClock.incrementClock();
            localMessage = new TCPMessage("node", "deleteFile", Main.localHostIP, backUpIP2, Main.localProcessClock.getClock());
            localMessage.fs533FileName = cmd.fs533FileName;
            localMessage.fileSaveType = "backUp2";

            Main.commandQueues.addCommandToOutBoundQueue(localMessage);
        }else
        {
            System.out.println("Deleting File Locally "+  cmd.fs533FileName);

            File file = new File(Main.fs533FileFolder + "/" + cmd.fs533FileName);
            file.delete();

            Main.localFileList.removeFileFromList(cmd.fs533FileName,"backUp2");
        }


    }

    private void sendGetToNode()
    {
        int index = Main.masterFileList.getIndexOfFile(cmd.fs533FileName);

        FileInfo temp = Main.masterFileList.getFileFromIndex(index);

        TCPMessage localMessage;

        String mainIP = temp.mainIP;
        if(!mainIP.equals(Main.localHostIP)) {

            ExecutorService fileXferInThread = Executors.newSingleThreadExecutor();
            fileXferInThread.execute(new fileTransferServer());

            Main.localProcessClock.incrementClock();
            localMessage = new TCPMessage("node", "get", Main.localHostIP, mainIP, Main.localProcessClock.getClock());
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

            Main.cacheFileName = cmd.fs533FileName;

        }else
        {
            if(!Main.cacheFileName.equals(cmd.fs533FileName)) {
                System.out.println("Caching File Locally " + cmd.fs533FileName);

                ReadWriteManager reader = new ReadWriteManager();
                reader.cacheFile(cmd.fs533FileName);
                Main.cacheFileName = cmd.fs533FileName;
            }else
            {
                System.out.println("File already in cache on Master");
            }

        }

        Main.localProcessClock.incrementClock();
        localMessage = new TCPMessage("node", "getReady", Main.localHostIP, cmd.senderIP, Main.localProcessClock.getClock());
        localMessage.fs533FileName = cmd.fs533FileName;
        localMessage.localFileName = cmd.localFileName;

        Main.commandQueues.addCommandToOutBoundQueue(localMessage);


    }
}
