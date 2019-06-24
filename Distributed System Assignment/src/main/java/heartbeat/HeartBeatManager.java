package heartbeat;

import commands.UdpCommandManager;
import core.GateWayManager;
import core.Main;

import java.util.Vector;

public class HeartBeatManager implements Runnable{

    public void run()
    {

        if(Main.processActive) {
            try {

                Vector<String> hbCheckResponse = Main.heartBeatTable.checkPredecessorTimeoutForFail();
                //System.out.println("HB Manager Check list: " + hbCheckResponse.size());
                if (hbCheckResponse.size() != 0) {
                    for (String ip : hbCheckResponse) {

                        UdpCommandManager.removeMachineFromCurrentList(ip);

                        if(ip.equals(Main.masterIPAddress))
                        {
                            Main.masterIPAddress = Main.localHostIP;
                            Main.inPortNum = 6000;
                            Main.outPortNum = 5000;
                        }
                    }

                    GateWayManager gateWayManager = new GateWayManager();

                    gateWayManager.updatePredecessorsList();
                    gateWayManager.updateSuccessorsList();

                    UdpCommandManager.updateAllMachines();
                }

            } catch (Exception e) {
                try {
                    Main.writeLog("Exception in HeartBeatManager" + e.getMessage());
                } catch (Exception e2) {
                    System.out.println("Generic logger error" + e2.getMessage());
                }
            }

        }

    }





}
