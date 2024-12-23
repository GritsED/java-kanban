package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager manager) {
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
            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                String response = gson.toJson(manager.getAllTasks());
                sendText(exchange, response, 200);
            } else if (pathParts.length == 3 && pathParts[1].equals("tasks") && idPath.isPresent()
                    && Pattern.matches("\\d+", pathParts[2])) {
                int id = idPath.get();
                if (manager.getTaskById(id) != null) {
                    String response = gson.toJson(manager.getTaskById(id));
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
            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                Task addedTask = manager.addNewTask(gson.fromJson(body, Task.class));
                if (Objects.nonNull(addedTask)) {
                    sendText(exchange, gson.toJson(addedTask), 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else if (pathParts.length == 3 && pathParts[1].equals("tasks") && idPath.isPresent()
                    && Pattern.matches("\\d+", pathParts[2])) {
                Task updatedTask = manager.updateTask(gson.fromJson(body, Task.class));
                if (Objects.nonNull(updatedTask)) {
                    sendText(exchange, gson.toJson(updatedTask), 201);
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
            Task deletedTask = manager.getTaskById(idPath.get());
            manager.deleteTaskById(idPath.get());

            try {
                if (Objects.isNull(deletedTask)) {
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
