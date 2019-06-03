package data;


import java.util.Date;
import java.util.Vector;

public class HeartBeatTable {

    private Vector<String> currentPredecessors;
    private Vector<Date> lastTimeStamp;

    public HeartBeatTable()
    {
        currentPredecessors =  new Vector<>();
        lastTimeStamp = new Vector<>();
    }

    public void addPredecessor(String ip)
    {
        Date date = new Date();
        currentPredecessors.add(ip);
        lastTimeStamp.add(date);
    }

    public void updateNewTimeStamp(String ip, Date timestamp)
    {
        int index = currentPredecessors.indexOf(ip);
        lastTimeStamp.set(index,timestamp);
    }

    public String checkPredecessorTimeoutForFail()
    {
        Date now = new Date();
        for (int i = 0; i < lastTimeStamp.size(); i++) {
            if((now.getTime() - lastTimeStamp.elementAt(i).getTime()) > 1000)
            {
                return currentPredecessors.elementAt(i);
            }
        }
        return "OK";
    }
}
