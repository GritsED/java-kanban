package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler {


    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();


        switch (requestMethod) {
            case "GET":
                handleGet(exchange, requestPath);
                break;
            case "POST":
                handlePost(exchange, requestPath);
                break;
            case "DELETE":
                handleDelete(exchange, requestPath);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String requestPath) throws IOException {
        String[] pathParts = requestPath.split("/");
        Optional<Integer> idPath = getId(requestPath);

        try {
            if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                String response = gson.toJson(manager.getAllSubtasks());
                sendText(exchange, response, 200);
            } else if (pathParts.length == 3 && pathParts[1].equals("subtasks") && idPath.isPresent()
                    && Pattern.matches("\\d+", pathParts[2])) {
                int id = idPath.get();
                if (manager.getSubtaskById(id) != null) {
                    String response = gson.toJson(manager.getSubtaskById(id));
                    sendText(exchange, response, 200);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, String requestPath) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), utf8);
        String[] pathParts = requestPath.split("/");
        Optional<Integer> idPath = getId(requestPath);

        try {
            if (body.isEmpty()) {
                sendText(exchange, "The body is empty", 400);
                return;
            }
            if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                Task addedSubtask = manager.addNewSubtask(gson.fromJson(body, Subtask.class));
                if (Objects.nonNull(addedSubtask)) {
                    sendText(exchange, gson.toJson(addedSubtask), 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else if (pathParts.length == 3 && pathParts[1].equals("subtasks") && idPath.isPresent()
                    && Pattern.matches("\\d+", pathParts[2])) {
                Task updatedSubtask = manager.updateSubtask(gson.fromJson(body, Subtask.class));
                if (Objects.nonNull(updatedSubtask)) {
                    sendText(exchange, gson.toJson(updatedSubtask), 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String requestPath) throws IOException {
        Optional<Integer> idPath = getId(requestPath);

        if (idPath.isPresent()) {
            Task deletedSubtask = manager.getSubtaskById(idPath.get());
            manager.deleteSubtaskById(idPath.get());

            try {
                if (Objects.isNull(deletedSubtask)) {
                    sendNotFound(exchange);
                } else {
                    sendText(exchange, "Task was deleted", 200);
                }
            } catch (Exception e) {
                sendInternalError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
