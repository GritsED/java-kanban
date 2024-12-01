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
    protected int id = 1;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Task addNewTask(Task newTask) {
        int newTaskId = generateNewId();
        newTask.setId(newTaskId);
        idToTask.put(newTask.getId(), newTask);
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
        int epicId = newSubtask.getEpicId();
        if (!idToEpic.containsKey(epicId)) {
            System.out.println("Такого эпика нет");
            return null;
        }
        int newSubtaskId = generateNewId();
        newSubtask.setId(newSubtaskId);
        idToSubtask.put(newSubtaskId, newSubtask);
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
        idToTask.put(task.getId(), task);
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
        System.out.println("Подзадача обновлена");
        updateEpicStatus(epic.getId());
        updateEpicEndTime(epic.getId());
        return subtask;
    }

    @Override
    public void deleteTaskById(Integer id) {
        Task task = idToTask.get(id);
        if (task == null) { // проверяем есть ли задача с таким id
            System.out.println("Такой задачи нет");
            return;
        }
        idToTask.remove(id);
        historyManager.remove(id);
        System.out.println("Задача удалена");
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = idToEpic.get(id);
        if (epic == null) {
            System.out.println("Такой задачи нет");
            return;
        }
        for (Integer subtaskId : epic.getSubtasksIds()) {
            idToSubtask.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
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
        for (Integer id : idToTask.keySet()) {
            historyManager.remove(id);
        }
        idToTask.clear();
        System.out.println("Задачи удалены");
    }

    @Override
    public void deleteAllEpics() {
        if (idToEpic.isEmpty()) {
            System.out.println("Список эпиков пуст");
            return;
        }
        for (Subtask subtask : idToSubtask.values()) {
            historyManager.remove(subtask.getId());
        }
        for (Integer epicId : idToEpic.keySet()) {
            historyManager.remove(epicId);
        }
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
        for (Integer subtaskId : idToSubtask.keySet()) {
            historyManager.remove(subtaskId);
        }
        idToSubtask.clear();
        for (Epic epic : idToEpic.values()) {
            epic.clearSubtasks();
            epic.setStatus(TaskStatus.NEW);
        }
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

    private void updateEpicEndTime (int epicId) {
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

    private int generateNewId() {
        return id++;
    }
}
