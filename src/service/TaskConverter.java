package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {

    public static Task fromString(String value) {
        String[] contents = value.split(",");
        int id = Integer.parseInt(contents[0]);
        String name = contents[2];
        String statusString = contents[3];
        String type = contents[1];
        TaskStatus status;
        String description = contents[4];
        Duration duration ;
        LocalDateTime startTime ;

        if (!type.equals("EPIC") && contents[7] != null && !contents[7].equals("null")) {
            duration = Duration.ofMinutes(Integer.parseInt(contents[6]));
            startTime = LocalDateTime.parse(contents[7]);
        } else {
            duration = Duration.ofMinutes(0);
            startTime = null;
        }

        status = switch (statusString) {
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> TaskStatus.NEW;
        };

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status, duration, startTime);
            case "EPIC":
                return new Epic(id, name, description, status);
            case "SUBTASK":
                int epicId = Integer.parseInt(contents[5]);
                return new Subtask(id, name, description, status, duration, startTime, epicId);
            default:
                return null;
        }
    }

    public static String toString(Task task) {
        if (task instanceof Subtask subtask) {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription(), subtask.getEpicId(), subtask.getDuration().toMinutes(),
                    subtask.getStartTime(), subtask.getEndTime());
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription(),"", task.getDuration().toMinutes(), task.getStartTime(), task.getEndTime());
        }
    }
}
