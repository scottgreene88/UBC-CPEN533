package commands;

import data.TCPMessage;

public class MasterCommandManager implements Runnable {

    private TCPMessage cmd;

    public MasterCommandManager(TCPMessage cmd)
    {
        this.cmd = cmd;
    }

    public void run()
    {

    }


}
