package heartbeat;

import commands.CommandManager;
import core.GateWayManager;
import core.Main;

import java.util.Vector;

public class HeartBeatManager implements Runnable{

    public void run()
    {


            try
            {

                Vector<String> hbCheckResponse = Main.heartBeatTable.checkPredecessorTimeoutForFail();

                if(hbCheckResponse.size() != 0)
                {
                    for (String ip: hbCheckResponse) {

                        CommandManager.removeMachineFromCurrentList(ip);
                    }

                    GateWayManager gateWayManager = new GateWayManager();

                    gateWayManager.updatePredecessorsList();
                    gateWayManager.updateSuccessorsList();

                    CommandManager.updateAllMachines();
                }

            }
            catch (Exception e)
            {
                try {
                    Main.writeLog("Exception in HeartBeatManager" + e.getMessage());
                }catch(Exception e2)
                {
                    System.out.println("Generic logger error" + e2.getMessage());
                }
            }



    }





}
