import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.addNewTask(new Task("Задача 1", "Описание 1"));
        Task task2 = taskManager.addNewTask(new Task("Задача 2", "Описание 2"));

        Epic epic1 = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic2 = taskManager.addNewEpic(new Epic("Эпик 2", "Описание эпика 2"));
        Epic epic3 = taskManager.addNewEpic(new Epic("Эпик 3", "Описание эпика 3"));

        System.out.println(taskManager.getAllEpics());


        Subtask subtask1 = taskManager.addNewSubtask(new Subtask("Подзадача 1 к эпику 1",
                "Описание 1", TaskStatus.NEW, epic1.getId()));
        Subtask subtask2 = taskManager.addNewSubtask(new Subtask("Подзадача 2 к эпику 1",
                "Описание 1", TaskStatus.DONE, epic1.getId()));

        Subtask subtask3 = taskManager.addNewSubtask(new Subtask("Подзадача 1 к эпику 2",
                "Описание 1", TaskStatus.DONE, epic2.getId()));

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-".repeat(20));


        Task updateTask1 = new Task(task1.getId(), "Задача 1", "Новое описание", TaskStatus.IN_PROGRESS);
        Task updateTask2 = new Task(task2.getId(), "Задача 2", "Новое описание", TaskStatus.DONE);
        System.out.println(taskManager.updateTask(updateTask1));
        System.out.println(taskManager.updateTask(updateTask2));
        System.out.println(taskManager.getAllTasks());
        System.out.println("-".repeat(20));


        Subtask updateSubtask11 = new Subtask("Подзадача 1 к эпику 1",
                "Новое описание подзадачи к эпику 1", TaskStatus.NEW, epic1.getId());
        Subtask updateSubtask12 = new Subtask("Подзадача 2 к эпику 1",
                "Новое описание подзадачи 1 2", TaskStatus.DONE, 15);
        updateSubtask11.setId(subtask1.getId());
        updateSubtask12.setId(subtask2.getId());
        taskManager.updateSubtask(updateSubtask11);
        taskManager.updateSubtask(updateSubtask12);
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-".repeat(20));

        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getSubtasksByEpic(epic1.getId()));
        System.out.println("-".repeat(20));

        taskManager.deleteTaskById(task1.getId());
        System.out.println(taskManager.getAllTasks());

        taskManager.deleteSubtaskById(subtask2.getId());
        taskManager.deleteEpicById(epic2.getId());

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-".repeat(20));

        taskManager.deleteAllTasks();
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("-".repeat(20));

        taskManager.deleteAllSubtasks();
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("-".repeat(20));

        taskManager.deleteAllEpics();
        System.out.println(taskManager.getAllEpics());
        System.out.println("-".repeat(20));



    }
}
