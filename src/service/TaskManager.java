package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task addNewTask(Task newTask);

    Epic addNewTask(Epic newEpic);

    Subtask addNewTask(Subtask newSubtask);

    Task updateTask(Task task);

    Epic updateTask(Epic epic);

    Subtask updateTask(Subtask subtask);

    void deleteTaskById(Integer id);

    void deleteEpicById(Integer id);

    void deleteSubtaskById(Integer id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();
}
