package test.java;

import main.manager.HistoryManager;
import main.manager.Managers;
import main.manager.Status;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class inMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistoryManager();
    }

    @Test
    void addTaskInHistory() {
        Task task = new Task("Task", "Test");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Содержание истории отличается");
        assertEquals(task, history.get(0), "Задание не совпадает");

    }

    @Test
    void whenTasksAreRepeated() {
        Task task = new Task("Task", "Test", 0, Status.NEW);
        Task task1 = new Task("Task1", "Test", 1, Status.NEW);
        Task task2 = new Task("Task2", "Test", 2, Status.NEW);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        int id = history.get(2).getId();
        assertEquals(3, history.size(), "Содержание истории отличается");
        assertEquals(task.getId(), id, "id не совпадает");
    }

    @Test
    void whenDeleteInHistory() {
        Task task = new Task("Task", "Test", 0, Status.NEW);
        Epic epic = new Epic("Epic", "Test", 1);
        SubTask subTask = new SubTask("Subtask", "Test", epic.getId(), 2, Status.NEW);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        List<Task> history1 = historyManager.getHistory();
        historyManager.remove(1);
        List<Task> history2 = historyManager.getHistory();
        assertNotEquals(history1.size(), history2.size(), "Задача не удалена из истории");
        assertEquals(history1.get(2), history2.get(1), "Задачи не последовательны");
    }
}
