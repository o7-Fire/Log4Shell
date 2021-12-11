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
        System.out.println("Headers: ");
        for (String key : exchange.getRequestHeaders().keySet()) {
            System.out.println(key + ": " + exchange.getRequestHeaders().get(key));
        }
        byte[] response = "Hello World".getBytes();//lol

        if (exchange.getRequestURI().getPath().startsWith(payloadPath)) {
            System.out.println("PAYLOAD!!!!!!!!!!");
            response = Main.getPayload(exchange.getRequestURI().getPath());
        }

        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
