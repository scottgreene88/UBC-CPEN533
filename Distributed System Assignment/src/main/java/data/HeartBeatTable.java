package data;


import core.Main;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.text.SimpleDateFormat;

public class HeartBeatTable {

    private ArrayList<String> currentPredecessors;
    private ArrayList<Long> lastTimeStamp;
    private ArrayList<Long> lastCheckedTimeStamp;

    public HeartBeatTable()
    {
        try {
            currentPredecessors = new ArrayList<>();
            lastTimeStamp = new ArrayList<>();
            lastCheckedTimeStamp =  new ArrayList<>();
        }
        catch (Exception e)
        {
            System.out.println("Exception in HB table");
        }
    }

    public synchronized void addPredecessor(String ip, long timeStamp)
    {
        try {

                //Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(dateString);
            //System.out.println("adding ip to pred list: " + ip);

                currentPredecessors.add(ip);
                lastCheckedTimeStamp.add(timeStamp);
                //System.out.println("Setting new machine ip: " + ip + " to last check time: " + date);
                //provide an offset to account for initial HB check
                //long dateOffset = date.getTime();
                //dateOffset += 10;
                //date.setTime(dateOffset);
                lastTimeStamp.add(timeStamp + 1);
                //System.out.println("Setting new machine ip: " + ip + " to time: " + date);

        }
            catch(Exception e)
        {
            System.out.println("Exception in HB table add pred");
        }

    }

    public synchronized void clearLists()
    {
        currentPredecessors.clear();
        lastCheckedTimeStamp.clear();
        lastTimeStamp.clear();
    }

    public synchronized void updateNewTimeStamp(String ip, long timestamp)
    {
        try {
        int index = currentPredecessors.indexOf(ip);
        if(index != -1) {
            //System.out.println("Updating Table with: " + ip + " with time: " + timestamp);
            lastTimeStamp.set(index, timestamp);
        }
        }
        catch (Exception e)
        {
            System.out.println("Exception in HB table set time" + e.getMessage());
        }
    }

    synchronized public Vector<String> checkPredecessorTimeoutForFail()
    {
        Vector<String> machineFailedList = new Vector<>();
        try {
            machineFailedList = new Vector<>();
            //System.out.println("Current HB List");
           // for (int i = 0; i < currentPredecessors.size(); i++) {
           //     System.out.println(currentPredecessors.get(i) + " last check: " + lastCheckedTimeStamp.get(i) + " last received: " + lastTimeStamp.get(i));
           // }

            while(Main.listBusyLock)
            {
                //System.out.println("Waiting for lock HB check");
                //if lists are being updated then wait for them to finish
                Thread.sleep(50);
            }

            Main.listBusyLock = true;

            for (int i = 0; i < currentPredecessors.size(); i++) {

                long lastChecked = lastCheckedTimeStamp.get(i);
                Thread.sleep(50);
                long lastReceived = lastTimeStamp.get(i);

                if (lastChecked == lastReceived) {
                    machineFailedList.add(currentPredecessors.get(i));
                    try {
                        Main.writeLog("Machine: " + currentPredecessors.get(i) + " has failed do to time stamp check - last checked " +
                                " - last received " + lastTimeStamp.get(i));
                    } catch (Exception e) {
                        System.out.println("Generic log error");
                    }
                } else {
                    //System.out.println("Updating last checked Machine: " + currentPredecessors.get(i));
                    lastCheckedTimeStamp.set(i, lastTimeStamp.get(i));
                }
            }
            Main.listBusyLock = false;

            return machineFailedList;
        }
        catch (Exception e)
        {
            Vector<String> emptyList = new Vector<>();
            return emptyList;
        }
    }
}
