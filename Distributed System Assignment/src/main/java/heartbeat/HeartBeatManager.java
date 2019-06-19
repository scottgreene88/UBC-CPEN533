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

                if (hbCheckResponse.size() != 0) {
                    for (String ip : hbCheckResponse) {

                        UdpCommandManager.removeMachineFromCurrentList(ip);

                        if(ip.equals(Main.masterIPAddress))
                        {
                            Main.masterIPAddress = Main.localHostIP;
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
