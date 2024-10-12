package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class TaskConverter {

    public static Task fromString(String value) {
        String[] contents = value.split(",");
        int id = Integer.parseInt(contents[0]);
        String name = contents[2];
        String statusString = contents[3];
        String type = contents[1];
        TaskStatus status;
        String description = contents[4];

        switch (statusString) {
            case "IN_PROGRESS":
                status = TaskStatus.IN_PROGRESS;
                break;
            case "DONE":
                status = TaskStatus.DONE;
                break;
            default:
                status = TaskStatus.NEW;
        }

        switch (type) {
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

    public static String toString(Task task) {
        if (task instanceof Subtask subtask) {
            return String.format("%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus()
                    , task.getDescription(), subtask.getEpicId());
        } else {
            return String.format("%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus()
                    , task.getDescription());
        }
    }
}
