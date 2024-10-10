package service;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            List<Task> allTasks = getAllTasks();
            List<Epic> allEpics = getAllEpics();
            List<Subtask> allSubtasks = getAllSubtasks();

            bw.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n");

            for (Task task : allTasks) {
                String taskAsString = task.toString();
                bw.write(taskAsString + "\n");
            }

            for (Epic epic : allEpics) {
                String epicAsString = epic.toString();
                bw.write(epicAsString + "\n");
            }

            for (Subtask subtask : allSubtasks) {
                String subtaskAsString = subtask.toString();
                bw.write(subtaskAsString + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving to file: " + e.getMessage());
        }
    }

    public static Task fromString(String value) {
        String[] contents = value.split(",");
        int id = Integer.parseInt(contents[0]);
        String name = contents[2];
        TaskStatus status;
        String description = contents[4];

        switch (contents[3]) {
            case "IN_PROGRESS":
                status = TaskStatus.IN_PROGRESS;
                break;
            case "Done":
                status = TaskStatus.DONE;
                break;
            default:
                status = TaskStatus.NEW;
        }

        switch (contents[1]) {
            case "TASK":
                return new Task(id, name, description, status);
            case "EPIC":
                return new Epic(id, name, description, status);
            case "SUBTASK":
                int epicId = Integer.parseInt(contents[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                return null;
        }

    }

    public static TaskManager loadFromFile(File file) throws ManagerLoadException {
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
            throw new ManagerLoadException("Error reading file: " + e.getMessage()); // свое исключение loadException
        }
        return taskManager;
    }

    @Override
    public Task addNewTask(Task newTask) {
        Task addedTask = super.addNewTask(newTask);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
        return addedTask;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        Epic addedEpic = super.addNewEpic(newEpic);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
        return addedEpic;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        Subtask addedSubtask = super.addNewSubtask(newSubtask);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
        return addedSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
        return updatedSubtask;
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();

        try {
            save();
        } catch (ManagerSaveException mse) {
            System.out.println(mse.getMessage());
        }
    }
}
