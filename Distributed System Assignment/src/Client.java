import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        String phrase =  scanner.nextLine();

        ExecutorService pool = Executors.newFixedThreadPool(ipVector.size());

        for(int i = 0; i < ipVector.size(); i++){
            pool.execute(new Client.clientThreader(ipVector.get(i), portNum, phrase));
        }

    }

    /*
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        try (Socket socket = new Socket(args[0], 59898)) {
            System.out.println("Enter lines of text then Ctrl+D or Ctrl+C to quit");
            Scanner scanner = new Scanner(System.in);
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (scanner.hasNextLine()) {
                out.println(scanner.nextLine());

                while(in.hasNextLine()) {
                    String response = in.nextLine();
                    System.out.println(response);
                }
                break;
                /*String[] splitResp = response.split(",");

                for(int i = 0; i < splitResp.length; i++)
                {
                    System.out.println(splitResp[i]);
                }
                //System.out.println(in.nextLine());

            }
        }
    }
*/

    private static class clientThreader implements Runnable {

        String ipAddress;
        int portNum;
        String phrase;

        clientThreader(String ipAddress, int portNum, String phrase)
        {
            this.ipAddress = ipAddress;
            this.portNum = portNum;
            this.phrase = phrase;
        }

        @Override
        public void run ()
        {
            try {
                Socket socket = new Socket(ipAddress, portNum);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println(phrase);

                    Scanner in = new Scanner(socket.getInputStream());

                    while(in.hasNextLine()) {
                        String response = in.nextLine();
                        System.out.println(response);
                    }

            }
            catch (Exception e)
            {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }
}
