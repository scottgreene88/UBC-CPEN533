

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;

import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

            InetAddress ip = InetAddress.getLocalHost();
            //String logFileName = "machine." + ip.getHostAddress() + ".log";
            //String logFileName = "B:\\School\\CPEN 533\\Assignments Repo\\UBC-CPEN533\\Distributed System Assignment\\out\\production\\Distributed System Assignment\\test_log1.log";
            String logFileName = "/home/ec2-user/Test/test_log.log";

            File file = new File(logFileName);

            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null){
                if(st.matches(phrase))
                {
                    responseList.add(st);
                }
            }

            System.out.println("Found " + responseList.size() +  " lines with string: " + phrase);

            return responseList;
        }
    }

}
