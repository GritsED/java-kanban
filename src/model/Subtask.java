package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, int epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description) {
        super(id, name, description);
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Duration duration,
                   LocalDateTime startTime, Integer epicId) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", id, getType(), name, status, description,
                duration, startTime, epicId);
    }

    @Override
    public Type getType() {
        return type = Type.SUBTASK;
    }
}
