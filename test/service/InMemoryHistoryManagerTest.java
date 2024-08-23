package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistoryManager();
    }

    @Test
    void getHistory_shouldAddTasksInHistory() {
        // prepare
        Task task1 = new Task("Task1", "Description");
        Task task2 = new Task("Task2", "Description");
        Task task3 = new Task("Task3", "Description");
        Task task4 = new Task("Task4", "Description");

        // do
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.addTask(task4);

        //check
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);

    }

    @Test
    void getHistory_shouldDeleteFirstTaskIfListBigger10() {
        //prepare
        for (int i = 0; i <= 13; i++) {
            Task task = new Task("Task " + i, "Dscription");
            historyManager.addTask(task);
        }

        // do
        List<Task> history = historyManager.getHistory();

        //check
        assertNotNull(history);
        assertEquals("Task 4", history.get(0).getName());
        assertEquals("Task 13", history.get(9).getName());
    }

}