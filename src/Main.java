import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task = new Task(0, "Name", "Description", TaskStatus.NEW, Duration.ofMinutes(0), LocalDateTime.now().plusDays(2));
        Task task1 = new Task(1, "Name1", "Description1", TaskStatus.IN_PROGRESS, Duration.ofMinutes(150), LocalDateTime.now());
        Task task2 = new Task(2, "Name2", "Description2", TaskStatus.DONE, Duration.ofMinutes(70), LocalDateTime.of(2024, 11, 30, 0, 0));

        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Epic epic = new Epic(3, "Epic", "Description");
        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask(4, "subtask", "Descr", TaskStatus.NEW,
                Duration.ofMinutes(1000), LocalDateTime.now(), epic.getId());
        Subtask subtask2 = new Subtask(5, "subtask2", "Descr2", TaskStatus.DONE,
                Duration.ofMinutes(3000), LocalDateTime.of(2024, 11, 28, 0, 0), epic.getId());
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask2);

        System.out.println("taskManager.getPrioritizedTasks() = " + taskManager.getPrioritizedTasks());


    }
}
