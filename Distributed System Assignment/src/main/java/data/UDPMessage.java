package data;

import java.util.Date;

public class UDPMessage {

    public String messageType;
    public String senderIP;
    public Date sendTimestamp;
    public String machineList;
    public String machineStartTimes;

    public UDPMessage(String messageType, String senderIP, Date sendTimestamp)
    {
        this.messageType = messageType;
        this.senderIP = senderIP;
        this.sendTimestamp = sendTimestamp;
    }

}
