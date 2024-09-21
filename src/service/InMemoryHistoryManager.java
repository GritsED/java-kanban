package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    static class Node {
        Node prev;
        Node next;
        Task task;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.next = next;
            this.task = task;
        }
    }

    private void linkLast(Task element) {
        Node oldTail = tail; // запоминаем ссылку на хвост
        Node newNode = new Node(tail, element, null); // создаем новый узел, без следующей ссылки
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> history = new LinkedList<>();
        Node currNode = head;

        while (currNode != null) {
            history.add(currNode.task);
            currNode = currNode.next;
        }
        return history;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        final Node prevNode = node.prev;
        final Node nextNode = node.next;

        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
            return;
        }

        if (prevNode == null) {
            nextNode.prev = null;
            head = nextNode;
        } else if (nextNode == null) {
            prevNode.next = null;
            tail = prevNode;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            Node removingNode = historyMap.remove(id);
            removeNode(removingNode);
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
        Node newTail = tail;
        historyMap.put(task.getId(), newTail);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
