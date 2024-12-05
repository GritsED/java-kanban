package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {
    private File file;
    private FileBackedTaskManager loadedManager;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedManager = Managers.getFileBackedTaskManager(file);
    }

    @BeforeEach
    void initFile() {
        createTaskManager();
        taskManager = loadedManager;
    }

    @Test
    void save_shouldSaveAndLoadEmptyFile() {

        assertTrue(loadedManager.getAllTasks().isEmpty(), "File must be empty");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "File must be empty");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "File must be empty");
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {

        Task task1 = new Task("Name", "Description");
        Task task2 = new Task("Name2", "Description2");
        Epic epic = new Epic("Name3", "Description3");

        loadedManager.addNewTask(task1);
        loadedManager.addNewTask(task2);
        loadedManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Name4", "Description4", epic.getId());
        Subtask subtask2 = new Subtask("Name5", "Description5", epic.getId());
        Subtask subtask3 = new Subtask("Name6", "Description6", epic.getId());

        loadedManager.addNewSubtask(subtask);
        loadedManager.addNewSubtask(subtask2);
        loadedManager.addNewSubtask(subtask3);


        assertEquals(2, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(3, taskManager.getAllSubtasks().size());
    }

    @Test
    void shouldCorrectParseStr() {
        // prepare
        Task task1 = new Task(1, "Name", "Description", TaskStatus.NEW);
        Task task2 = new Task(2, "Name2", "Description2", TaskStatus.NEW, duration, ldt1);
        Epic epic = new Epic(3, "Name3", "Description3");
        Subtask subtask = new Subtask(4, "Name4", "Description4", TaskStatus.IN_PROGRESS, duration,
                ldt2, epic.getId());
        Subtask subtask2 = new Subtask(5, "Name5", "Description5", TaskStatus.DONE, duration,
                ldt3, epic.getId());

        //do
        loadedManager.addNewTask(task1);
        loadedManager.addNewTask(task2);
        loadedManager.addNewEpic(epic);
        loadedManager.addNewSubtask(subtask);
        loadedManager.addNewSubtask(subtask2);

        //check
        assertEquals(Duration.ofMinutes(0), loadedManager.getAllTasks().get(0).getDuration()); //1
        assertNull(loadedManager.getAllTasks().get(0).getStartTime()); //1

        assertEquals(Duration.ofMinutes(30), loadedManager.getAllTasks().get(1).getDuration()); //2
        assertEquals(LocalDateTime.of(2024, 12, 1, 0, 0),
                loadedManager.getAllTasks().get(1).getStartTime()); //2

        assertEquals(Duration.ofMinutes(60), loadedManager.getAllEpics().get(0).getDuration()); //3
        assertEquals(LocalDateTime.of(2024, 12, 2, 0, 0),
                loadedManager.getAllEpics().get(0).getStartTime()); //3
        assertEquals(LocalDateTime.of(2024, 12, 3, 0, 30),
                loadedManager.getAllEpics().get(0).getEndTime()); //3

    }
}