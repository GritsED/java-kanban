package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static service.FileBackedTaskManager.loadFromFile;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {
    private File file;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileBackedTaskManager taskManager;
        return taskManager = Managers.getFileBackedTaskManager(file);
    }

    @Test
    void save_shouldSaveAndLoadEmptyFile() {
        FileBackedTaskManager EmptyFile = createTaskManager();
        FileBackedTaskManager loadedManager = Managers.getFileBackedTaskManager(file);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "File must be empty");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "File must be empty");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "File must be empty");
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        FileBackedTaskManager file1 = createTaskManager();

        Task task1 = new Task("Name", "Description");
        Task task2 = new Task("Name2", "Description2");
        Epic epic = new Epic("Name3", "Description3");

        file1.addNewTask(task1);
        file1.addNewTask(task2);
        file1.addNewEpic(epic);

        Subtask subtask = new Subtask("Name4", "Description4", epic.getId());
        Subtask subtask2 = new Subtask("Name5", "Description5", epic.getId());
        Subtask subtask3 = new Subtask("Name6", "Description6", epic.getId());

        file1.addNewSubtask(subtask);
        file1.addNewSubtask(subtask2);
        file1.addNewSubtask(subtask3);

        TaskManager loadedManager = loadFromFile(file);

        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(3, loadedManager.getAllSubtasks().size());

    }
}