package test.java;

import main.manager.HistoryManager;
import main.manager.Managers;
import main.manager.TaskManager;
import main.manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class inMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistoryManager();
    @BeforeEach
            void getManager(){
        TaskManager manager = Managers.getDefaultTaskManager();
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
    void whenGetHistoryUpdaitTask() {
        Task task = new Task("TTask", "Test ");
        historyManager.add(task);
        Task correctTask = task;
        Task newTask = new Task("Task44444444", "Test ");
        task = newTask;
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(correctTask, history.get(0), "содержание не соответствует");
        assertEquals(task, history.get(1), "содержание не соответствует");
    }

    @Test
    void shouldNotExceedHistoryLimit() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task " + i, "Test " + i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "Размер истории не совпадает");
        assertEquals("Task 6", history.get(0).getName(), "задача не совпадает с элементом истории");
        assertEquals("Task 15", history.get(9).getName(), "задача не совпадает с элементом истории");
    }
}
