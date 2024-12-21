package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange exchange, String text, Integer code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        try (OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(resp);
        }
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String message = "Not Found";
        sendText(exchange, message, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String message = "Not Acceptable";
        sendText(exchange, message, 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String message = "Internal Server Error";
        sendText(exchange, message, 500);
    }
}
