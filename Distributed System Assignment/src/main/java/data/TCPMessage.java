package data;

public class TCPMessage {

    public String messageType;
    public String commandType;
    public String senderIP;
    public long sendTimestamp;
    public String machineList;
    public String machineStartTimes;
    public String dataList;


    public TCPMessage(String messageType, String commandType, String senderIP, long sendTimestamp)
    {
        this.commandType = commandType;
        this.messageType = messageType;
        this.senderIP = senderIP;
        this.sendTimestamp = sendTimestamp;
    }



}
