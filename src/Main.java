import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.addNewTask(new Task("Задача 1", "Описание 1"));
        Task task2 = taskManager.addNewTask(new Task("Задача 2", "Описание 2"));
        Epic epic1 = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask1 = taskManager.addNewSubtask(new Subtask("Подзадача 1 к эпику 1",
                "Описание 1", TaskStatus.NEW, epic1.getId()));
        Subtask subtask2 = taskManager.addNewSubtask(new Subtask("Подзадача 2 к эпику 1",
                "Описание 1", TaskStatus.DONE, epic1.getId()));
        Subtask subtask3 = taskManager.addNewSubtask(new Subtask("Подзадача 3 к эпику 1",
                "Описание 1", TaskStatus.DONE, epic1.getId()));
        Epic epic2 = taskManager.addNewEpic(new Epic("Эпик 2", "Описание эпика 2"));

        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getTaskById(task2.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getEpicById(epic2.getId()));
        System.out.println(taskManager.getSubtaskById(subtask1.getId()));
        System.out.println(taskManager.getSubtaskById(subtask2.getId()));
        System.out.println(taskManager.getSubtaskById(subtask3.getId()));
        System.out.println("-".repeat(50));

        printHistory(taskManager);
        System.out.println("-".repeat(50));

        System.out.println(taskManager.getTaskById(task2.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getSubtaskById(subtask1.getId()));
        System.out.println(taskManager.getSubtaskById(subtask2.getId()));
        System.out.println(taskManager.getEpicById(epic2.getId()));
        System.out.println(taskManager.getSubtaskById(subtask3.getId()));
        System.out.println("-".repeat(50));

        printHistory(taskManager);
        System.out.println("-".repeat(50));

        taskManager.deleteTaskById(task1.getId());
        printHistory(taskManager);
        System.out.println("-".repeat(50));

        taskManager.deleteEpicById(epic1.getId());
        printHistory(taskManager);
        System.out.println("-".repeat(50));


    }

    public static void printHistory(TaskManager taskManager) {
        System.out.println("History");
        for (Task history : taskManager.getHistory()) {
            System.out.println(history);
        }
    }
}