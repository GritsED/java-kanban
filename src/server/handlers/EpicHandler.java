package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager manager) {
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
            if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                String response = gson.toJson(manager.getAllEpics());
                sendText(exchange, response, 200);
            } else if (pathParts.length == 3 && pathParts[1].equals("epics") && idPath.isPresent()
                    && Pattern.matches("\\d+", pathParts[2])) {
                int id = idPath.get();
                if (manager.getEpicById(id) != null) {
                    String response = gson.toJson(manager.getEpicById(id));
                    sendText(exchange, response, 200);
                } else {
                    sendNotFound(exchange);
                }
            } else if (pathParts.length == 4 && pathParts[1].equals("epics") && idPath.isPresent()
                    && Pattern.matches("\\d+", pathParts[2]) && pathParts[3].equals("subtasks")) {
                Epic epicById = manager.getEpicById(idPath.get());
                if (Objects.nonNull(epicById)) {
                    String response = gson.toJson(manager.getSubtasksByEpic(epicById.getId()));
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
            if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                Epic addedEpic = manager.addNewEpic(gson.fromJson(body, Epic.class));
                if (Objects.nonNull(addedEpic)) {
                    sendText(exchange, gson.toJson(addedEpic), 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else if (pathParts.length == 3 && pathParts[1].equals("epics") && idPath.isPresent()
                    && Pattern.matches("\\d+", pathParts[2])) {
                Epic updatedEpic = manager.updateEpic(gson.fromJson(body, Epic.class));
                if (Objects.nonNull(updatedEpic)) {
                    sendText(exchange, gson.toJson(updatedEpic), 201);
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
            Task deletedTask = manager.getEpicById(idPath.get());
            manager.deleteEpicById(idPath.get());

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
