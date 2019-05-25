
package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import main.*;

public class udpMessageServer
{
    private int portNum;
    private DatagramSocket ds;

    public udpMessageServer(int portNumIn) throws IOException
    {
        this.portNum = portNumIn;

        try
        {
            this.ds = new DatagramSocket(portNum);
        }
        catch(SocketException e)
        {
            String errorText = "UDP Server Exception: " + e.getMessage();

            if(Main.development) {
                System.out.println(errorText);
            }

            Main.log.writeLogLine(errorText);

        }

    }

    public void startListening() throws IOException
    {
        byte[] receive = new byte[65535];

        DatagramPacket DpReceive = null;
        while (true)
        {

            // create a DatgramPacket to receive the data.
            DpReceive = new DatagramPacket(receive, receive.length);

            // recieve the data in byte buffer.
            ds.receive(DpReceive);

            StringBuilder message = data(receive);

            if(Main.development) {
                System.out.println("Received message: " + message);
            }

            Main.log.writeLogLine("Received message: " + message);

            // Clear the buffer after every message.
            receive = new byte[65535];
        }

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
