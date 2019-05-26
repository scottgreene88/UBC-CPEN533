package heartbeat;

import java.net.InetAddress;
import java.util.concurrent.*;

public class heartBeatManager {

    private int rightPortNum;
    private int leftPortNum;
    private InetAddress leftIpAddress;
    private InetAddress rightIpAddress;

    public heartBeatManager( int leftPortNum, int rightPortNum,InetAddress leftIpAddress,InetAddress rightIpAddress)
    {
        this.leftPortNum = leftPortNum;
        this.rightPortNum = rightPortNum;
        this.leftIpAddress = leftIpAddress;
        this.rightIpAddress =  rightIpAddress;
    }

    public void startHb()
    {

        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleWithFixedDelay( new sendHeartBeat(leftPortNum, rightPortNum, leftIpAddress, rightIpAddress) ,0, 5, TimeUnit.SECONDS);


    }

}
