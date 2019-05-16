import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;

public class Server {


    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(59898)) {
            System.out.println("The server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new handleGrep(listener.accept()));
            }
        }
    }

    private static class handleGrep implements Runnable {
        private Socket socket;

        handleGrep(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


                while (in.hasNextLine()) {


                    //out.println(in.nextLine().toUpperCase());
                    Vector<String> response = findPhrase(in.nextLine());
                    for(int i = 0; i < response.size(); i++)
                    {
                        out.println(response.get(i));
                    }


                    break;
                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }

        public Vector<String> findPhrase(String phrase) throws Exception
        {
            Vector<String> responseList = new Vector<>();

            System.out.println("Finding lines with string: " + phrase);
            //Do work below
            //Code below here needs to find the strings and create a Vector of the lines
            //this is an example of creating just a simple vector from 0-49
            //I am fine with hard coding the log path so that the log is always in the same place
            //We can have the logger running as a seperate program for right now.

            InetAddress ip = InetAddress.getLocalHost();
            for(int i = 1;i < 6;i++)
            {
                responseList.add(String.valueOf(i) + " " + ip.toString());
            }

            //Do work above
            System.out.println("Found " + responseList.size() +  " lines with string: " + phrase);
            return responseList;
        }
    }

}
