package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add_shouldAddTask(){
        //prepare
        Task task1 = new Task(0, "Task1", "Description");

        // do
        historyManager.add(task1);

        //check
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(task1, history.getFirst());
    }

    @Test
    void add_shouldSaveOrder() {
        // prepare
        Task task1 = new Task(1, "Task1", "Description");
        Task task2 = new Task(2, "Task2", "Description");
        Task task3 = new Task(3, "Task3", "Description");
        Task task4 = new Task(4, "Task4", "Description");

        // do
        historyManager.add(task1);
        historyManager.add(task4);
        historyManager.add(task3);
        historyManager.add(task2);

        //check
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(4, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task4, history.get(1));
        assertEquals(task2, history.get(3));

    }

    @Test
    void add_shouldMoveTaskToEnd() {
        //prepare
        Task task1 = new Task(1, "Task1", "Description1");
        Task task2 = new Task(2, "Task2", "Description2");

        //do
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // Добавляем task1 снова

        //check
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0)); // task2 должен остаться первым
        assertEquals(task1, history.get(1)); // task1 перемещен в конец

    }

    @Test
    void add_shouldAddSameTasksOneTime() {
        //prepare
        Task task = new Task(1, "Task2", "Description");

        //do
        historyManager.add(task);
        historyManager.add(task);

        //check
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void add_shouldNotAddNullTask() {
        //prepare
        Task task = null;

        //do
        historyManager.add(task);

        //check
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void remove_shouldRemoveOneTask() {
        //prepare
        Task task = new Task(1, "Task1", "Description");

        historyManager.add(task);

        //do
        historyManager.remove(task.getId());

        //check
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void remove_shouldRemoveTask() {
        // prepare
        Task task1 = new Task(1, "Task1", "Description");
        Task task2 = new Task(2, "Task2", "Description");
        Task task3 = new Task(3, "Task3", "Description");
        Task task4 = new Task(4, "Task4", "Description");

        //do
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        //check
        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size());
        historyManager.remove(2);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
    }
}