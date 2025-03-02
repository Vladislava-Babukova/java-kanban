package main.manager;

import main.manager.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node node = new Node();
        node.task = task;
        linkLast(node);

        nodeMap.put(node.task.getId(), node);
    }

    public void linkLast(Node node) {
        final Node oldTail = tail;
        tail = node;
        if (oldTail == null) {
            head = node;
        } else
            oldTail.next = node;
        node.prew = oldTail;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node node = head;
        while (Objects.nonNull(node)) {
            result.add(node.getTask());
            node = node.next;
        }
        return result;
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
            nodeMap.remove(id);
        }
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        final Node next = node.next;
        final Node prew = node.prew;
        node.task = null;
        if (nodeMap.size() == 1) {
            node.next = null;
            node.prew = null;
            head = null;
            tail = null;

        }
        if (node.prew == null) {
            head = next;
        } else {
            node.prew.next = next;
        }
        if (node.next == null) {
            tail = prew;
        } else {
            node.next.prew = prew;
        }
    }


    public static class Node {
        Node next;
        Node prew;
        Task task;

        public Task getTask() {
            return task;
        }
    }

}


