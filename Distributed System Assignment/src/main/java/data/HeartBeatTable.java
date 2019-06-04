package data;


import java.util.Date;
import java.util.Vector;

public class HeartBeatTable {

    private Vector<String> currentPredecessors;
    private Vector<Date> lastTimeStamp;

    public HeartBeatTable()
    {
        try {
            currentPredecessors = new Vector<>();
            lastTimeStamp = new Vector<>();
        }
        catch (Exception e)
        {
            System.out.println("Exception in HB table");
        }
    }

    public void addPredecessor(String ip)
    {
        try {
        Date date = new Date();
        currentPredecessors.add(ip);
        lastTimeStamp.add(date);
        }
            catch (Exception e)
        {
            System.out.println("Exception in HB table add pred");
        }
    }

    public void updateNewTimeStamp(String ip, Date timestamp)
    {
        try {
        int index = currentPredecessors.indexOf(ip);
        lastTimeStamp.set(index,timestamp);
        }
                catch (Exception e)
        {
            System.out.println("Exception in HB table set time");
        }
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
