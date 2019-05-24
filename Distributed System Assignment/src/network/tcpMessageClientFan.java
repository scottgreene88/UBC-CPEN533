package network;

import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.Scanner;

import main.*;

public class tcpMessageClientFan  {

    private int portNum;
    private Socket socket;
    private Vector<String> ipVector;


    tcpMessageClientFan(int portNumIn, Vector<String> ipVectorIn) throws IOException
    {
        this.ipVector = ipVector;
        this.portNum = portNumIn;

    }


    public void sendFanMessage(String message)
    {

        ExecutorService pool = Executors.newFixedThreadPool(ipVector.size());

        //Vector<Future<Vector<String>>> responseList = new Vector<Vector<String>>(ipVector.size());

        long startTime = System.nanoTime();

        for (int i = 0; i < ipVector.size(); i++) {
            pool.submit(new tcpMessageClientFan.clientThreader(ipVector.get(i), portNum, message));
        }



        pool.shutdown();
        try {
            pool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        long endTime = System.nanoTime();

        System.out.println("Total time in milliseconds = " + (endTime - startTime) / 1000000);

    }

    public Vector<String> clientThread(String ipAddress, int portNum, String message) throws IOException {
        Vector<String> responseVector = new Vector<>();


        try {
            this.socket = new Socket(ipAddress, portNum);
            socket.setSoTimeout(10000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(message);

            Scanner in = new Scanner(socket.getInputStream());

            while(in.hasNextLine()) {
                String response = in.nextLine();

                responseVector.add(response);

            }

        }
        catch(java.net.SocketTimeoutException e)
        {
            if(Main.development) {
                System.out.println("Socket Timeout Exception: " + e.getMessage());
            }

            Main.log.writeLogLine("Socket Timeout Exception: " + e.getMessage());

        }
        catch (Exception e)
        {
            if(Main.development) {
                System.out.println("Socket Exception: " + e.getMessage());
            }

            Main.log.writeLogLine("Socket Exception: " + e.getMessage());
        }finally {
            try { socket.close(); } catch (IOException e) {

                if(Main.development) {
                    System.out.println("Socket Exception: " + e.getMessage());
                }

                Main.log.writeLogLine("Socket Exception: " + e.getMessage());

            }

        }

        return responseVector;
    }


    public class clientThreader implements Callable<Vector<String>> {

        private String ipAddress;
        private int portNum;
        private String phrase;
        private Socket socket;


        clientThreader(String ipAddress, int portNum, String message)
        {
            this.ipAddress = ipAddress;
            this.portNum = portNum;
            this.phrase = message;
        }

        @Override
        public Vector<String> call () throws IOException {
            Vector<String> responseVector = new Vector<>();

            try {
                this.socket = new Socket(ipAddress, portNum);
                socket.setSoTimeout(10000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println(phrase);

                Scanner in = new Scanner(socket.getInputStream());

                while(in.hasNextLine()) {
                    String response = in.nextLine();
                    responseVector.add(response);

                    if(Main.development) {
                        System.out.println(ipAddress + ": " + response);
                    }
                    Main.log.writeLogLine("Server: " + ipAddress + " response: " + response);
                }


            }
            catch(java.net.SocketTimeoutException e)
            {
                if(Main.development) {
                    System.out.println("Socket Timeout Exception: " + e.getMessage());
                }

                Main.log.writeLogLine("Socket Timeout Exception: " + e.getMessage());

            }
            catch (Exception e)
            {
                if(Main.development) {
                    System.out.println("Socket Exception: " + e.getMessage());
                }

                Main.log.writeLogLine("Socket Exception: " + e.getMessage());
            }finally {
                try { socket.close(); } catch (IOException e) {

                    if(Main.development) {
                        System.out.println("Socket Exception: " + e.getMessage());
                    }

                    Main.log.writeLogLine("Socket Exception: " + e.getMessage());

                }

            }

            return responseVector;
        }
    }
}

