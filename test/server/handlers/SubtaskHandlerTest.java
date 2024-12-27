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

class SubtaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    Epic epic;

    SubtaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
        epic = new Epic("Epic", "Description");
        manager.addNewEpic(epic);

    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void handlePOST_ShouldAddSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.
                ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void handlePOST_shouldUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 11, 30, 9,30), epic.getId());
        manager.addNewSubtask(subtask);

        Subtask subtask1 = new Subtask(2,"TestUpdate", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 11, 30, 9,40), epic.getId());
        String subtaskJson = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.
                ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("TestUpdate", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void handleGET_shouldGetTask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(2,"Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 11, 30, 9,30), epic.getId());
        Subtask newSubtask = manager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNotNull(newSubtask, "Задачи не возвращаются");
        assertEquals(2, newSubtask.getId(), "Некорректное количество задач");
        assertEquals("Test 2", newSubtask.getName(), "Некорректное имя задачи");
    }

    @Test
    public void handleDelete_shouldDeleteTask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(2,"Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 11, 30, 9,30), epic.getId());
        manager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getAllSubtasks().size(), "Задачи не возвращаются");
    }

    @Test
    public void handleGET_shouldReturn404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void handleDELETE_shouldReturn404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void handlePOST_shouldNotUpdateCrossoverTask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 11, 30, 9,30), epic.getId());
        manager.addNewSubtask(subtask);

        Subtask subtask1 = new Subtask(2,"TestUpdate", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 11, 30, 9,30), epic.getId());
        String subtaskJson = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.
                ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

}