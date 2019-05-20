import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args)  {

        //Testing hardcode variables, replace with config style later
        int portNum = 59898;

        Vector<String> ipVector = new Vector<>();

        //Development
        ipVector.add("localhost");

        //EC2 instances

/*
        ipVector.add("172.31.23.204");
        ipVector.add("172.31.29.195");
        ipVector.add("172.31.17.62");
        ipVector.add("172.31.29.58");
        ipVector.add("172.31.30.52");
  */

        System.out.println("Enter lines of text to search for");
        Scanner scanner = new Scanner(System.in);

        while(scanner.hasNextLine()) {
            String phrase =  scanner.nextLine();


            ExecutorService pool = Executors.newFixedThreadPool(ipVector.size());



                long startTime = System.nanoTime();

                for (int i = 0; i < ipVector.size(); i++) {
                    pool.execute(new Client.clientThreader(ipVector.get(i), portNum, phrase));
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
    }



    private static class clientThreader implements Runnable {

        private String ipAddress;
        private int portNum;
        private String phrase;
        private Socket socket;

        public int searchCounter;

        clientThreader(String ipAddress, int portNum, String phrase)
        {
            this.ipAddress = ipAddress;
            this.portNum = portNum;
            this.phrase = phrase;
            this.searchCounter = 0;
        }

        @Override
        public void run ()
        {
            try {
                this.socket = new Socket(ipAddress, portNum);
                socket.setSoTimeout(10000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println(phrase);

                    Scanner in = new Scanner(socket.getInputStream());

                    while(in.hasNextLine()) {
                        String response = in.nextLine();
                        searchCounter++;
                        System.out.println(ipAddress + ": " + response);
                    }
                System.out.println(ipAddress + " Found lines: " + searchCounter);
            }
            catch(java.net.SocketTimeoutException e)
            {
                System.out.println("Timeout Exception on " + ipAddress +": " +  e.getMessage());
            }
            catch (Exception e)
            {
                System.out.println("Exception on " + ipAddress +": " +  e.getMessage());
            }finally {
                try { socket.close(); } catch (IOException e) {
                    System.out.println("Exception on " + ipAddress +": " +  e.getMessage());
                }

            }
        }
    }
}
