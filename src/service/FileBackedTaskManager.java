package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.util.List;

import static service.TaskConverter.fromString;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            List<Task> allTasks = getAllTasks();
            List<Epic> allEpics = getAllEpics();
            List<Subtask> allSubtasks = getAllSubtasks();

            bw.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n");

            for (Task task : allTasks) {
                String taskAsString = TaskConverter.toString(task);
                bw.write(taskAsString + "\n");
            }

            for (Epic epic : allEpics) {
                String epicAsString = TaskConverter.toString(epic);
                bw.write(epicAsString + "\n");
            }

            for (Subtask subtask : allSubtasks) {
                String subtaskAsString = TaskConverter.toString(subtask);
                bw.write(subtaskAsString + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving to file: " + e.getMessage());
        }
    }

    public static TaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            int maxId = 0;
            while (br.ready()) {
                String line = br.readLine();
                Task task = fromString(line);

                if (maxId > taskManager.id) {
                    taskManager.id = maxId + 1;
                }

                if (task != null) {
                    maxId = task.getId();
                    switch (task.getType()) {
                        case TASK:
                            taskManager.idToTask.put(task.getId(), task);
                            break;
                        case EPIC:
                            taskManager.idToEpic.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            taskManager.idToSubtask.put(task.getId(), (Subtask) task);
                            break;
                    }
                }


                for (Subtask sub : taskManager.idToSubtask.values()) {
                    Epic epic = taskManager.idToEpic.get(sub.getEpicId());
                    epic.addSubtasksId(sub.getId());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error reading file: " + e.getMessage()); // свое исключение loadException
        }
        return taskManager;
    }

    @Override
    public Task addNewTask(Task newTask) {
        Task addedTask = super.addNewTask(newTask);
        save();
        return addedTask;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        Epic addedEpic = super.addNewEpic(newEpic);
        save();
        return addedEpic;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        Subtask addedSubtask = super.addNewSubtask(newSubtask);
        save();
        return addedSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
