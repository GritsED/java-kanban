package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager manager;
    protected final Charset utf8 = StandardCharsets.UTF_8;
    protected final Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    protected BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

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

    protected Optional<Integer> getId(String requestPath) {
        String[] pathParts = requestPath.split("/");

        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
