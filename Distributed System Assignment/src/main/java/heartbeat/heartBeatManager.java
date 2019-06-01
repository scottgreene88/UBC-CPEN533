package heartbeat;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.*;

import core.Main;
import network.*;

public class heartBeatManager implements Runnable{

    private int rightPortNum;
    private int leftPortNum;
    private InetAddress leftIpAddress;
    private InetAddress rightIpAddress;

    public long hbTime = 1000;
    public long hbTimeDelay = 500;

    public heartBeatManager( int leftPortNum, int rightPortNum,InetAddress leftIpAddress,InetAddress rightIpAddress)
    {
        this.leftPortNum = leftPortNum;
        this.rightPortNum = rightPortNum;
        this.leftIpAddress = leftIpAddress;
        this.rightIpAddress =  rightIpAddress;
    }

    public void run()
    {
        try {

            String leftMessage = "", rightMessage = "";


            //starts sending heartbeats at regular intervals
            ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
            es.scheduleWithFixedDelay(new sendHeartBeat(leftPortNum, rightPortNum, leftIpAddress, rightIpAddress), 0, hbTime, TimeUnit.MILLISECONDS);

            //future values for listen events
            Future<String> leftBackBeat;
            Future<String> rightBackBeat;


            ExecutorService executor = Executors.newFixedThreadPool(2);

            //Test code below
            //Random r = new Random();
            //rightPortNum =  r.nextInt(5000);

            while (true) {


                //listen on ports for message
                leftBackBeat = executor.submit(new udpMessageServer(leftPortNum));
                rightBackBeat = executor.submit(new udpMessageServer(rightPortNum));


                try {
                    //try to read the future messages. We generally dont care what the message is but will record for logging
                    leftMessage = leftBackBeat.get(hbTime + hbTimeDelay, TimeUnit.MILLISECONDS);
                    rightMessage = rightBackBeat.get(hbTime + hbTimeDelay, TimeUnit.MILLISECONDS);
                } catch (ExecutionException | InterruptedException e) {
                    if (Main.development) {
                        System.out.println("Exception from hbManager: " + e.getMessage());
                    }
                    Main.log.writeLogLine("Exception hbManager: " + e.getMessage());
                } catch (TimeoutException e) {


                    //If this exception is triggered then it means that one of the events timed out.

                    if (Main.development) {
                        System.out.println("Timeout exception found. Left message: " + leftMessage + " , Right message: " + rightMessage);
                    }
                    Main.log.writeLogLine("Timeout exception found. Left message: " + leftMessage + " , Right message: " + rightMessage);


                    if (!leftBackBeat.isDone()) {
                        notifyGroupOfFailure(leftPortNum, leftIpAddress);

                        reOrgAfterFailure(rightPortNum, rightIpAddress);

                        executor.shutdownNow();

                        es.shutdown();
                        es.awaitTermination(1, TimeUnit.SECONDS);
                        break;
                    }
                    if (!rightBackBeat.isDone()) {
                        notifyGroupOfFailure(rightPortNum, rightIpAddress);

                        reOrgAfterFailure(leftPortNum, leftIpAddress);
                        executor.shutdownNow();

                        es.shutdown();
                        es.awaitTermination(1, TimeUnit.SECONDS);
                        break;
                    }


                }

                //reset the messages
                leftMessage = "";
                rightMessage = "";

                //Temp code deal with future result of message
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    System.out.println("Exception when reading Future hbMan" + e.getMessage());
                }

            }
        }
        catch (Exception e)
        {
            System.out.println("Generic Exception caught in hbManager run(): " + e.getMessage());
        }

    }

    private void notifyGroupOfFailure(int portNum, InetAddress ipAddress) throws IOException
    {
        if(Main.development) {
            System.out.println("Missed message from: " + portNum + " , " + ipAddress.getHostAddress());
        }
        Main.log.writeLogLine("Missed message from: " + portNum + " , " + ipAddress.getHostAddress());

        //Test code below
        rightPortNum = 5678;

    }

    private void reOrgAfterFailure(int portNum, InetAddress ipAddress) throws IOException
    {
        boolean result;

        tcpMessageClient backNode =  new tcpMessageClient(1526, ipAddress.getHostAddress());
        result  = backNode.sendSingleMessage("REORG");

    }
}
