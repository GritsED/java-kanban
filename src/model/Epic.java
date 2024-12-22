package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    protected LocalDateTime endTime;
    private List<Integer> subtasksIds = new ArrayList<>();

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

    public Epic(String name, String description, TaskStatus status, Duration duration,
                 LocalDateTime startTime, LocalDateTime endTime) {
        super(name, description, status, duration, startTime);
        this.endTime = endTime;
    }

    @Override
    public Type getType() {
        return type = Type.EPIC;
    }

    public List<Integer> getSubtasksIds() {
        if (Objects.nonNull(subtasksIds)) {
            return new ArrayList<>(subtasksIds);
        } else {
            return new ArrayList<>();
        }
    }

    public void addSubtasksId(int subtaskId) {
        if (Objects.isNull(subtasksIds)) {
            subtasksIds = new ArrayList<>();
        }
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
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", id, getType(), name, status, description, duration,
                startTime, endTime);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
