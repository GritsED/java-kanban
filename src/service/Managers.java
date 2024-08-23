package service;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager historyManager = getDefaultHistoryManager();
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
