package itzbenz;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class RequestHandler implements HttpHandler {

    String payloadPath = "/itzbenz/payload/";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //log request
        System.out.println();
        System.out.println();
        System.out.println("Request: " + exchange.getRequestURI() + " " + exchange.getRequestMethod());
        System.out.println("Address: " + exchange.getRemoteAddress());
        System.out.println("User-Agent: " + exchange.getRequestHeaders().getFirst("User-Agent"));
        byte[] response = "Hello World".getBytes();//lol

        if (exchange.getRequestURI().getPath().startsWith(payloadPath)) {
            System.out.println("PAYLOAD!!!!!!!!!!");
            response = Main.getPayload(exchange.getRequestURI().getPath());
        }
        if (exchange.getRequestURI().getPath().startsWith("/information")) {
            System.out.println("INFORMATION!!!!!!!!!!");
            System.out.println();
            String parsed = exchange.getRequestURI().getPath().substring("/information".length());
            parsed = parsed.replace("/", " ");
            //remove .class at the end
            if (parsed.endsWith(".class")) parsed = parsed.substring(0, parsed.length() - 6);
            System.out.println("Parsed: " + parsed);
            System.out.println();
        }

        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
