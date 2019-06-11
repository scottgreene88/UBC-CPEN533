import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class NewClient {


    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.println("Enter command to execute: list, disconnect or grep");
            Scanner scanner = new Scanner(System.in);

            String command = scanner.nextLine();

            {
                try {

                    InetAddress ipAddress = InetAddress.getByName("localhost");
                    Socket socket = new Socket(ipAddress, 5000);

                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    out.println(command);

                    Scanner in = new Scanner(socket.getInputStream());

                    while (in.hasNextLine()) {
                        String response = in.nextLine();
                        System.out.println(response);
                    }

                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }
        }
    }
}
