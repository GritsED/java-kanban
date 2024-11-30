package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    protected LocalDateTime endTime;
    private final List<Integer> subtasksIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Epic(Integer id, String name, String description, TaskStatus status, Duration duration,
                LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, description, status, duration, startTime);
        this.endTime = endTime;
    }

    @Override
    public Type getType() {
        return type = Type.EPIC;
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtasksId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    public void clearSubtasks() {
        subtasksIds.clear();
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", id, getType(), name, status, description);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
