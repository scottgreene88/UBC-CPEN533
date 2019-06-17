package data;

public class TCPMessage {

    public String messageType;
    public String senderIP;
    public long sendTimestamp;
    public String machineList;
    public String machineStartTimes;
    public String grepPhrase;


    public TCPMessage(String messageType, String senderIP, long sendTimestamp)
    {
        this.messageType = messageType;
        this.senderIP = senderIP;
        this.sendTimestamp = sendTimestamp;
    }

}
