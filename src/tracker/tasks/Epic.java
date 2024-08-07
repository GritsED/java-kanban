package tracker.tasks;

import java.util.ArrayList;

public class Epic  extends Task{

    private final ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtasksId (int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId (Integer subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    public void clearSubtasks () {
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
