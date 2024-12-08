package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryTaskManagerTest extends AbstractTaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return Managers.getDefault();
    }

    @Test
    void isCrossover_shouldNotAddTaskIsCrossover() {
        // prepare
        Task task = new Task(0, "Name", "Description", TaskStatus.NEW, duration, ldt1);
        Task task1 = new Task(1, "Name1", "Description1", TaskStatus.IN_PROGRESS, duration, ldt1);

        // do
        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);

        //check
        assertEquals(1, taskManager.getAllTasks().size());

    }

    @Test
    void isCrossover_shouldSaveTaskIfIsNotCrossover() {
        // prepare
        Task task = new Task(0, "Name", "Description", TaskStatus.NEW, duration, ldt1);
        Task task1 = new Task(1, "Name1", "Description1", TaskStatus.IN_PROGRESS, duration, ldt2);
        Task task2 = new Task(2, "Name2", "Description2", TaskStatus.DONE, duration, ldt3);


        // do
        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        //check
        assertEquals(3, taskManager.getAllTasks().size());
    }

    @Test
    void addTaskInSet_shouldNotAddEpicAndIfStartTimeNull() {
        //prepare
        Task task = new Task(0, "Name", "Description", TaskStatus.NEW, duration, null);
        Epic epic = new Epic(1, "Name1", "Description1", TaskStatus.IN_PROGRESS, duration,
                null, null);

        //do
        taskManager.addTaskInSet(task);
        taskManager.addTaskInSet(epic);

        //check
        assertEquals(0, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void updateEpicEndTime_shouldUpdateEpicEndTime() {
        //prepare
        Epic epic = new Epic(1, "Name1", "Description1", TaskStatus.IN_PROGRESS);
        Epic epic1 = new Epic(2, "Name2", "Description1");
        Subtask subtask1 = new Subtask(3, "subtask", "Descr", TaskStatus.NEW,
                duration, ldt1, epic1.getId());
        Subtask subtask2 = new Subtask(4, "subtask2", "Descr2", TaskStatus.DONE,
                duration, ldt3, epic1.getId());

        //do
        taskManager.addNewEpic(epic);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        //check
        assertEquals(Duration.ofMinutes(60), epic1.getDuration());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(LocalDateTime.of(2024, 12, 3, 0, 30), epic1.getEndTime());
        taskManager.deleteEpicById(2);
        assertEquals(0, taskManager.getPrioritizedTasks().size());


    }

}