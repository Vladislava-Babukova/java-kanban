package projectTest;

import main.manager.InMemoryTaskManager;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    InMemoryTaskManager imManager;

    @Override
    protected InMemoryTaskManager getNewManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void whenTimeCrosses() {
        imManager = getNewManager();
        imManager.deleteAllTasks();
        Task task = new Task("Task1", "1", "20.03.2024 11:10", 10);
        imManager.addTask(task);
        Epic epic = new Epic("Эпик", "1");
        imManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "2", epic.getId(), "20.03.2024 11:10", 10);
        imManager.addSubTask(subTask);
        assertEquals(1, imManager.getPrioritizedTasks().size(), "Размер списка не совпадает");
        SubTask subTask1 = new SubTask("Subtask1", "2", epic.getId(), "20.03.2024 11:21", 10);
        imManager.addSubTask(subTask1);
        assertEquals(2, imManager.getPrioritizedTasks().size(), "Размер списка не совпадает");
        SubTask subTask2 = new SubTask("Subtask2", "2", epic.getId(), "20.03.2024 11:15", 10);
        imManager.addSubTask(subTask2);
        assertEquals(2, imManager.getPrioritizedTasks().size(), "Размер списка не совпадает");
    }

}