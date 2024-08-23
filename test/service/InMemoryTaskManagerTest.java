package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask_shouldSaveTask() {
        //prepare
        Task task = new Task("Задача", "Описание");
        Task expectedTask = new Task(1, "Задача", "Описание");

        // do
        Task actualTask = taskManager.addNewTask(task);

        //check
        assertNotNull(actualTask);
        assertNotNull(actualTask.getId());
        assertEquals(expectedTask, actualTask);
    }

    @Test
    void addNewEpic_shouldSaveTask() {
        //prepare
        Epic epic = new Epic("Эпик", "Описание");
        Epic expectedEpic = new Epic(1, "Эпик", "Описание");

        // do
        Task actualEpic = taskManager.addNewTask(epic);

        //check
        assertNotNull(actualEpic);
        assertNotNull(actualEpic.getId());
        assertEquals(expectedEpic, actualEpic);
    }

    @Test
    void addNewSubtask_shouldSaveTaskWithEpic() {
        //prepare
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("Subtask", "Description", 1);
        Subtask expectedSubtask = new Subtask(2, "Subtask", "Description", 1);

        // do
        Subtask actualSubtask = taskManager.addNewTask(subtask);

        //check
        assertNotNull(actualSubtask);
        assertNotNull(actualSubtask.getId());
        assertEquals(expectedSubtask, actualSubtask);
    }

    @Test
    void addNewSubtask_shouldSaveTaskWithoutEpic() {
        //prepare
        Subtask subtask = new Subtask("Subtask", "Description", 1);

        // do
        Subtask actualSubtask = taskManager.addNewTask(subtask);

        //check
        assertNull(actualSubtask);
    }

    @Test
    void updateTask_shouldUpdateTaskWithSpecifiedId() {
        //prepare
        Task task = new Task("Задача2", "Описание2");
        Task savedTask = taskManager.addNewTask(task);
        Task updateTask = new Task(savedTask.getId(), "Задача2_updated", "Описание2_updated");
        Task expectedUpdateTask = new Task(savedTask.getId(), "Задача2_updated", "Описание2_updated");

        // do
        Task actualUpdatedTask = taskManager.updateTask(updateTask);

        // check
        assertEquals(expectedUpdateTask, actualUpdatedTask);
    }

    @Test
    void updateTask_shouldUpdateEpicWithSpecifiedId() {
        //prepare
        Epic epic = new Epic("Epic_2", "Description_2");
        Epic savedEpic = taskManager.addNewTask(epic);
        Epic updateEpic = new Epic(savedEpic.getId(), "Epic_2_updated", "Description_2_updated");
        Epic expectedUpdateEpic = new Epic(savedEpic.getId(), "Epic_2_updated", "Description_2_updated");

        // do
        Epic actualUpdatedEpic = taskManager.updateTask(updateEpic);

        // check
        assertEquals(expectedUpdateEpic, actualUpdatedEpic);
    }

    @Test
    void updateTask_shouldUpdateSubtaskWithSpecifiedId() {
        //prepare
        Epic epic = new Epic("Epic_2", "Description_2");
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("Subtask_1", "Description_1", 1);
        Subtask savedSubtask = taskManager.addNewTask(subtask);
        Subtask updateSubtask = new Subtask(savedSubtask.getId(), "Subtask_1_updated", "Description_1_updated", 1);
        Subtask expectedUpdateSubtask = new Subtask(savedSubtask.getId(), "Subtask_1_updated", "Description_1_updated", 1);

        // do
        Subtask actualUpdatedSubtask = taskManager.updateTask(updateSubtask);

        // check
        assertEquals(expectedUpdateSubtask, actualUpdatedSubtask);
    }

    @Test
    void deleteTask_shouldDeleteTaskById() {
        // prepare
        Task task = new Task("Task_1", "Description_1");
        Task savedTask = taskManager.addNewTask(task);

        // do
        int taskId = savedTask.getId();
        taskManager.deleteTaskById(taskId);
        Task deletedTask = taskManager.getTaskById(taskId);

        // check
        assertNull(deletedTask);
    }

    @Test
    void deleteTask_shouldDeleteEpicById() {
        // prepare
        Epic epic = new Epic("Epic_1", "Description_1");
        Epic savedEpic = taskManager.addNewTask(epic);

        // do
        int epicId = savedEpic.getId();
        taskManager.deleteEpicById(epicId);
        Epic deletedEpic = taskManager.getEpicById(epicId);

        // check
        assertNull(deletedEpic);
    }

    @Test
    void deleteTask_shouldDeleteSubtaskById() {
        // prepare
        Epic epic = new Epic("Epic_1", "Description_1");
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("Subtask", "Description", 1);
        Subtask savedSubtask = taskManager.addNewTask(subtask);

        // do
        int subtaskId = savedSubtask.getId();
        taskManager.deleteSubtaskById(subtaskId);
        Subtask deletedSubtask = taskManager.getSubtaskById(subtaskId);

        // check
        assertNull(deletedSubtask);
    }

    @Test
    void deleteAllTasks_shouldDeleteAllTasks() {
        // prepare
        Task task1 = new Task("Task_1", "Description");
        Task task2 = new Task("Task_2", "Description");
        Task task3 = new Task("Task_3", "Description");
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        // do
        // check
        assertNotNull(taskManager.getAllTasks());
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void deleteAllTasks_shouldDeleteAllEpics() {
        // prepare
        Epic epic1 = new Epic("Epic_1", "Description");
        Epic epic2 = new Epic("Epic_1", "Description");
        Epic epic3 = new Epic("Epic_1", "Description");
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(epic2);
        taskManager.addNewTask(epic3);

        // do
        // check
        assertNotNull(taskManager.getAllEpics());
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void deleteAllTasks_shouldDeleteAllSubtasks() {
        // prepare
        Epic epic = new Epic("Epic_1", "Description");
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Description", 1);
        Subtask subtask2 = new Subtask("Subtask2", "Description", 1);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);

        // do
        // check
        assertNotNull(taskManager.getAllSubtasks());
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void getAllTasks_shouldGetAllTasks() {
        // prepare
        Task task1 = new Task("Task_1", "Description");
        Task task2 = new Task("Task_2", "Description");
        Task task3 = new Task("Task_3", "Description");

        Task expectedTask1 = new Task(1, "Task_1", "Description");
        Task expectedTask2 = new Task(2, "Task_2", "Description");
        Task expectedTask3 = new Task(3, "Task_3", "Description");


        //do
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        //check
        assertEquals(expectedTask1, taskManager.getAllTasks().get(0));
        assertEquals(expectedTask2, taskManager.getAllTasks().get(1));
        assertEquals(expectedTask3, taskManager.getAllTasks().get(2));
    }

    @Test
    void getAllTasks_shouldGetAllEpics() {
        // prepare
        Epic epic1 = new Epic("Epic_1", "Description");
        Epic epic2 = new Epic("Epic_2", "Description");
        Epic epic3 = new Epic("Epic_3", "Description");

        Epic expectedEpic1 = new Epic(1, "Epic_1", "Description");
        Epic expectedEpic2 = new Epic(2, "Epic_2", "Description");
        Epic expectedEpic3 = new Epic(3, "Epic_3", "Description");


        //do
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(epic2);
        taskManager.addNewTask(epic3);

        //check
        assertEquals(expectedEpic1, taskManager.getAllEpics().get(0));
        assertEquals(expectedEpic2, taskManager.getAllEpics().get(1));
        assertEquals(expectedEpic3, taskManager.getAllEpics().get(2));
    }

    @Test
    void getAllTasks_shouldGetAllSubtasks() {
        //prepare
        Epic epic = new Epic("Epic_1", "Description");
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Description", 1);
        Subtask subtask2 = new Subtask("Subtask2", "Description", 1);

        Subtask expectedSubtask1 = new Subtask(2, "Subtask2", "Description", 1);
        Subtask expectedSubtask2 = new Subtask(3, "Subtask2", "Description", 1);


        //do
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);

        //check
        assertEquals(expectedSubtask1, taskManager.getAllSubtasks().get(0));
        assertEquals(expectedSubtask2, taskManager.getAllSubtasks().get(1));

    }

    @Test
    void getAllTasks_shouldGetAllEpicSubtasks() {
        //prepare
        Epic epic = new Epic("Epic_1", "Description");
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Description", 1);
        Subtask subtask2 = new Subtask("Subtask2", "Description", 1);

        Subtask expectedSubtask1 = new Subtask(2, "Subtask2", "Description", 1);
        Subtask expectedSubtask2 = new Subtask(3, "Subtask2", "Description", 1);


        //do
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);

        //check
        assertEquals(expectedSubtask1, taskManager.getSubtasksByEpic(epic.getId()).get(0));
        assertEquals(expectedSubtask2, taskManager.getSubtasksByEpic(epic.getId()).get(1));

    }
    
    @Test
    void updateEpicStatus_shouldUpdateEpicStatusToDone() {
        //prepare
        Epic epic = new Epic("Epic_1", "Description");
        taskManager.addNewTask(epic);
        Subtask expectedSubtask1 = new Subtask( "Subtask2", "Description", TaskStatus.NEW, 1);
        Subtask expectedSubtask2 = new Subtask( "Subtask2", "Description", TaskStatus.NEW, 1);
        taskManager.addNewTask(expectedSubtask1);
        taskManager.addNewTask(expectedSubtask2);

        //do
        Subtask updatedSubtask1 = new Subtask(2, "Subtask2_update", "Description", TaskStatus.DONE, 1);
        Subtask updatedSubtask2 = new Subtask(3,"Subtask2_update", "Description", TaskStatus.DONE, 1 );

        //check
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.updateTask(updatedSubtask1);
        taskManager.updateTask(updatedSubtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus());



    }
}
