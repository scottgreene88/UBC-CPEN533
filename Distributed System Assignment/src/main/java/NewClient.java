import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import com.google.gson.Gson;

public class NewClient {



    public static void main(String[] args) throws Exception {

        //NewClient client = new NewClient();
        //test

            while (true) {

                System.out.println("Enter command to execute: list, disconnect or grep");
                Scanner scanner = new Scanner(System.in);

                String commandType = scanner.nextLine();


                    try {

                        String ipAddress = InetAddress.getLocalHost().getHostAddress();
                        TCPMessage tcpMessage = new TCPMessage ("Test", commandType, ipAddress, 173);

                        Socket socket = new Socket(ipAddress, 5000);

                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println(toJson(tcpMessage));

                        socket.close();

                        try {

                            ServerSocket listener = new ServerSocket (6000);
                            Socket s = null;
                            s = listener.accept();

                            Scanner in = new Scanner(s.getInputStream());
                            //while (in.hasNextLine()) {
                                String response = in.nextLine();
                                TCPMessage receivedMessage = parseJason(response);
                                //System.out.println(receivedMessage.dataList);
                            //}

                            Gson json = new Gson();
                            String[] temp = json.fromJson(receivedMessage.dataList,String[].class);
                            Vector<String> tempList = new Vector<>();
                            for(int i = 0; i < temp.length; i++)
                            {
                                tempList.add(temp[i]);
                            }

                            for (String val: tempList
                                 ) {
                                System.out.println(val);
                            }


                            listener.close();
                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage()); }

                } catch (Exception e) {
                        System.out.println ("Exception: " + e.getMessage()); }
            }
        }

    public static String toJson (Object tcpMessage) {
        Gson json = new Gson();
        String jsonArray = json.toJson(tcpMessage);
        return (jsonArray);}

    public static TCPMessage parseJason (String message) {
        Gson gson = new Gson();
        TCPMessage parsedTCPMessage = gson.fromJson(message,TCPMessage.class );
        return parsedTCPMessage;
    }

    public static class TCPMessage {

        public String messageType;
        public String commandType;
        public String senderIP;
        public long sendTimestamp;
        public String machineList;
        public String machineStartTimes;
        public String dataList;


        public TCPMessage(String messageType, String commandType, String senderIP, long sendTimestamp)
        {
            this.commandType = commandType;
            this.messageType = messageType;
            this.senderIP = senderIP;
            this.sendTimestamp = sendTimestamp;
        }



    }
    }


