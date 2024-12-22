package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    EpicHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void handlePOST_ShouldAddTask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic1", "Testing epic 2",
                TaskStatus.NEW, Duration.ofMinutes(0), null, null);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.
                ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic1", epicsFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void handlePOST_shouldUpdateTask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic1", "Testing epic 2",
                TaskStatus.NEW, Duration.ofMinutes(0), null, null);
        manager.addNewEpic(epic);

        Epic updateTask = new Epic(1,"Epic1Update", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));
        String taskJson = gson.toJson(updateTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.
                ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic1Update", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void handleGET_shouldGetTask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic1", "Testing epic 2",
                TaskStatus.NEW, Duration.ofMinutes(0), null, null);
        Epic newEpic = manager.addNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> allEpics = manager.getAllEpics();

        assertNotNull(newEpic, "Задачи не возвращаются");
        assertEquals(1, newEpic.getId(), "Некорректное количество задач");
        assertEquals("Epic1", newEpic.getName(), "Некорректное имя задачи");
        assertEquals(1,allEpics.size());
    }

    @Test
    public void handleDelete_shouldDeleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic1", "Testing epic 2",
                TaskStatus.NEW, Duration.ofMinutes(0), null, null);
        manager.addNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getAllEpics().size(), "Задачи не возвращаются");
    }

    @Test
    public void handleGET_shouldReturn404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void handleDELETE_shouldReturn404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void handleGET_shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic( "Epic1", "Testing epic 2",
                TaskStatus.NEW, Duration.ofMinutes(0), null, null);
        manager.addNewEpic(epic);
        Subtask subtask = new Subtask(2,"Subtask", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(2, epic.getSubtasksIds().get(0));

    }

}