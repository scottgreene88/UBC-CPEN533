package data;

import java.util.Date;

public class UDPMessage {

    public String messageType;
    public String senderIP;
    public long sendTimestamp;
    public String machineList;
    public String machineStartTimes;

    public String masterNodeIP;

    public UDPMessage(String messageType, String senderIP, long sendTimestamp)
    {
        this.messageType = messageType;
        this.senderIP = senderIP;
        this.sendTimestamp = sendTimestamp;
    }

}
