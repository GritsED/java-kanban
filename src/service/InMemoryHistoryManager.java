package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private static final int MAX_HISTORY_LIST = 10;

    @Override
    public void addTask(Task task) {
        if (history.size() >= MAX_HISTORY_LIST) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history); // возвращаем копию списка, чтобы нельзя было вносить изменения в основной лист
    }
}
