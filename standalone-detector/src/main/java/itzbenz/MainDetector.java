package itzbenz;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//LDAPListener
public class MainDetector {
    public static int port = 1389;
    public static String address = "0.0.0.0";

    public static void main(String[] args) throws IOException {
        if (args.length != 0) {
            String[] addressAndPort = args[0].split(":");
            address = addressAndPort[0];
            if (addressAndPort.length == 2) {
                port = Integer.parseInt(addressAndPort[1]);
            }
        }
        System.out.println();
        System.out.println();
        System.out.println("${jndi:ldap://" + address + ":" + port + "/o=reference}");
        System.out.println();
        System.out.println();
        System.out.println("Listening on " + address + ":" + port);
        ServerSocket serverSocket = new ServerSocket(port, 50, java.net.InetAddress.getByName(address));
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Accepted connection from " + socket.getInetAddress().getHostAddress() + ":" +
                        socket.getPort());
            }
        }
    }
}
