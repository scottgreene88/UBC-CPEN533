
package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Callable;

import core.*;

public class UdpMessageServer implements Callable<String>
{
    private int portNum;
    private DatagramSocket ds;

    public UdpMessageServer(int portNumIn) throws IOException
    {
        this.portNum = portNumIn;

    }

    @Override
    public String call()
    {

        String message = "";
        try {
            try {
                this.ds = new DatagramSocket(portNum);
            } catch (SocketException e) {
                Main.writeLog("UDP Server Exception on portnum: " + portNum + "  " +  e.getMessage());
            }


            try {

                message = startListening();

            } catch (Exception e) {
                Main.writeLog("UDP Server Exception on portnum: " + portNum + "  " +  e.getMessage());
            }


        }
        catch (Exception e)
        {
            System.out.println("Generic Exception caught in hbManager run(): " + e.getMessage());
        }

        ds.close();
        return message;
    }

    private String startListening() throws IOException
    {
        byte[] receive = new byte[65535];

        DatagramPacket DpReceive = null;


            // create a DatagramPacket to receive the data.
            DpReceive = new DatagramPacket(receive, receive.length);

            // receive the data in byte buffer.
            ds.receive(DpReceive);

            StringBuilder message = data(receive);

        //if(!message.toString().contains("HB")) {
            Main.writeLog("Received message: " + message);
        //}

        ds.close();
        return message.toString();
    }


    // A utility method to convert the byte array
    // data into a string representation.
    private static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
