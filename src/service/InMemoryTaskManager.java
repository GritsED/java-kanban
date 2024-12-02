package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> idToTask = new HashMap<>();
    protected final Map<Integer, Epic> idToEpic = new HashMap<>();
    protected final Map<Integer, Subtask> idToSubtask = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int id = 1;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Task addNewTask(Task newTask) {
        if (isTasksCrossover(newTask)) {
            System.out.println("The start time is the same as for the existing task.");
            return null;
        }
        int newTaskId = generateNewId();
        newTask.setId(newTaskId);
        idToTask.put(newTask.getId(), newTask);
        addTaskInSet(newTask);
        System.out.println("Задача добавлена");
        return newTask;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        int newEpicId = generateNewId();
        newEpic.setId(newEpicId);
        idToEpic.put(newEpic.getId(), newEpic);
        updateEpicEndTime(newEpicId);
        System.out.println("Эпик добавлен");
        return newEpic;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        if (isTasksCrossover(newSubtask)) {
            System.out.println("The start time is the same as for the existing subtask.");
            return null;
        }
        int epicId = newSubtask.getEpicId();
        if (!idToEpic.containsKey(epicId)) {
            System.out.println("Такого эпика нет");
            return null;
        }
        int newSubtaskId = generateNewId();
        newSubtask.setId(newSubtaskId);
        idToSubtask.put(newSubtaskId, newSubtask);
        addTaskInSet(newSubtask);
        Epic epic = idToEpic.get(epicId);
        epic.addSubtasksId(newSubtaskId);
        updateEpicStatus(epicId);
        updateEpicEndTime(epicId);
        System.out.println("Подзадача добавлена");
        return newSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = idToTask.get(task.getId());
        if (updatedTask == null) {
            System.out.println("Такой задачи нет");
            return null;
        }
        prioritizedTasks.remove(updatedTask);

        if (isTasksCrossover(task)) {
            System.out.println("The start time is the same as for the existing task.");
            return null;
        }
        idToTask.put(task.getId(), task);
        addTaskInSet(task);
        System.out.println("Задача обновлена");
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = idToEpic.get(epic.getId());
        if (updatedEpic == null) {
            System.out.println("Такого эпика нет");
            return null;
        }
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());
        updateEpicEndTime(epic.getId());
        System.out.println("Эпик обновлен");
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (isTasksCrossover(subtask)) {
            System.out.println("The start time is the same as for the existing task.");
            return null;
        }

        Subtask updatedSubtask = idToSubtask.get(subtask.getId());
        if (updatedSubtask == null) {
            System.out.println("Такой подзадачи нет");
            return null;
        }
        Epic epic = idToEpic.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такого эпика нет");
            return null;
        }
        idToSubtask.put(subtask.getId(), subtask);
        addTaskInSet(subtask);
        System.out.println("Подзадача обновлена");
        updateEpicStatus(epic.getId());
        updateEpicEndTime(epic.getId());
        return subtask;
    }

    @Override
    public void deleteTaskById(Integer id) {
        Task task = idToTask.get(id);
        if (task == null) {
            System.out.println("Такой задачи нет");
            return;
        }
        idToTask.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
        System.out.println("Задача удалена");
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = idToEpic.get(id);
        if (epic == null) {
            System.out.println("Такой задачи нет");
            return;
        }
        epic.getSubtasksIds().forEach(subtaskId -> {
            Subtask subtask = idToSubtask.get(subtaskId);
            if (subtask != null) prioritizedTasks.remove(subtask);
            idToSubtask.remove(subtaskId);
            historyManager.remove(subtaskId);
        });

        historyManager.remove(id);
        idToEpic.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Subtask subtask = idToSubtask.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = idToEpic.get(subtask.getEpicId());
        historyManager.remove(id);
        prioritizedTasks.remove(subtask);
        epic.removeSubtaskId(id);
        updateEpicStatus(epic.getId());
        updateEpicEndTime(epic.getId());
    }

    @Override
    public void deleteAllTasks() {
        if (idToTask.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }
        idToTask.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeAll(idToTask.values());
        idToTask.clear();
        System.out.println("Задачи удалены");
    }

    @Override
    public void deleteAllEpics() {
        if (idToEpic.isEmpty()) {
            System.out.println("Список эпиков пуст");
            return;
        }
        idToSubtask.values().stream().mapToInt(Task::getId).forEach(historyManager::remove);
        idToEpic.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeAll(idToSubtask.values());
        idToEpic.clear();
        idToSubtask.clear();
        System.out.println("Эпики удалены");
    }

    @Override
    public void deleteAllSubtasks() {
        if (idToSubtask.isEmpty()) {
            System.out.println("Список подзадач пуст");
            return;
        }
        idToSubtask.keySet().forEach(historyManager::remove);
        idToSubtask.clear();
        prioritizedTasks.removeAll(idToSubtask.values());
        idToEpic.values().forEach(epic -> {
            epic.clearSubtasks();
            epic.setStatus(TaskStatus.NEW);
        });
        System.out.println("Подзадачи удалены");
    }

    @Override
    public Task getTaskById(int id) {
        Task task = idToTask.get(id);

        if (idToTask.containsKey(id)) {
            historyManager.add(task);
            return idToTask.get(id);
        } else {
            System.out.println("Задачи нет");
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic task = idToEpic.get(id);

        if (idToEpic.containsKey(id)) {
            historyManager.add(task);
            return idToEpic.get(id);
        } else {
            System.out.println("Эпика нет");
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask task = idToSubtask.get(id);

        if (idToSubtask.containsKey(id)) {
            historyManager.add(task);
            return idToSubtask.get(id);
        } else {
            System.out.println("Подзадачи нет");
            return null;
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(idToTask.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(idToEpic.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(idToSubtask.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        if (idToEpic.containsKey(epicId)) {
            Epic epic = idToEpic.get(epicId);
            List<Integer> subtaskIds = epic.getSubtasksIds();
            ArrayList<Subtask> epicSubtasks = new ArrayList<>();
            for (int id : subtaskIds) {
                epicSubtasks.add(idToSubtask.get(id));
            }
            return epicSubtasks;
        } else {
            System.out.println("Такой подзадачи нет");
            return null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = idToEpic.get(epicId);
        int counterNew = 0;
        int counterDone = 0;
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        for (int id : epic.getSubtasksIds()) {
            if (idToSubtask.get(id).getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            } else if (idToSubtask.get(id).getStatus() == TaskStatus.DONE) {
                counterDone++;
            } else if (idToSubtask.get(id).getStatus() == TaskStatus.NEW) {
                counterNew++;
            }
        }
        if (counterNew == epic.getSubtasksIds().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (counterDone == epic.getSubtasksIds().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void updateEpicEndTime(int epicId) {
        Epic epic = idToEpic.get(epicId);

        if (epic.getSubtasksIds().isEmpty()) {
            epic.setDuration(Duration.ofMinutes(0));
            epic.setEndTime(null);
            epic.setStartTime(null);
            return;
        }

        List<Subtask> subtasks = getSubtasksByEpic(epicId);

        Duration duration = subtasks.stream().map(Subtask::getDuration).filter(Objects::nonNull)
                .reduce(Duration.ofMinutes(0), Duration::plus);
        LocalDateTime startTime = subtasks.stream().map(Subtask::getStartTime).filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).orElse(null);
        LocalDateTime endTime = subtasks.stream().map(Subtask::getEndTime).filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(null);

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private int generateNewId() {
        return id++;
    }

    private void addTaskInSet(Task task) {
        if (task.getStartTime() == null || task instanceof Epic) {
            return;
        }
        prioritizedTasks.add(task);
    }

    private boolean isCrossover(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime endTime2 = task2.getEndTime();

        return startTime1.isBefore(endTime2) && endTime1.isAfter(startTime2);
    }

    private boolean isTasksCrossover(Task newTask) {
        return prioritizedTasks.stream().filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .anyMatch(task -> isCrossover(task, newTask));
    }
}
