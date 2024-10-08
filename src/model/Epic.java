package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
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
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasksIds=" + subtasksIds +
                '}';
    }
}
