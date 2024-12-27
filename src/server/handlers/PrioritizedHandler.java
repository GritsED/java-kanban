package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");

            if (pathParts.length == 2 && pathParts[1].equals("prioritized")) {
                if ("GET".equals(requestMethod)) {
                    String response = gson.toJson(manager.getPrioritizedTasks());
                    sendText(exchange, response, 200);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
