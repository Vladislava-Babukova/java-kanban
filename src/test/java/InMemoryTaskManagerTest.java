package test.java;

import main.manager.Managers;
import main.manager.Status;
import main.manager.TaskManager;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager manager = Managers.getDefaultTaskManager();

    @Test
    void whenAddTask() {
        Task task = new Task("Task", "1");
        manager.addTask(task);

        final Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTusks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.getName(), savedTask.getName(), "Имя не совпадает");
        assertEquals(task.getDetails(), savedTask.getDetails(), "Описание не совпадает");
    }

    @Test
    void whenAddEpic() {
        Epic epic = new Epic("Epic", "1");
        manager.addEpic(epic);
        final int epicId = epic.getId();

        final Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final ArrayList<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic.getName(), savedEpic.getName(), "Имя не совпадает");
        assertEquals(epic.getDetails(), savedEpic.getDetails(), "Описание не совпадает");
    }

    @Test
    void whenAddSubTask() {
        Epic epic = new Epic("Epic", "1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "1", epic.getId());
        manager.addSubTask(subTask);
        final int subTaskId = subTask.getId();

        final SubTask savedSubTask = manager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

        final List<SubTask> subTasks = manager.getAllSubTusks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask.getName(), savedSubTask.getName(), "Имя не совпадает");
        assertEquals(subTask.getDetails(), savedSubTask.getDetails(), "Описание не совпадает");
    }


    @Test
    void whenAddEpicInSubTask() {
        Epic epic = new Epic("Epic", "1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "1", epic.getId(), epic.getId(), Status.NEW);
        manager.addSubTask(subTask);
        int subtaskId = subTask.getId();
        int epicId = epic.getId();
        assertNotEquals(subtaskId, epicId, "ID совпадают");
    }

    @Test
    void WhenAddSubTaskInEpic() {
        SubTask subTask = new SubTask("subTask", "1", 20, 20, Status.IN_PROGRESS);
        manager.addSubTask(subTask);
        Epic epic = new Epic("Epic", "1", 20);
        manager.addEpic(epic);
        int subtaskId = subTask.getId();
        int epicId = epic.getId();
        assertNotEquals(epicId, subtaskId, "ID совпадают");
    }

    @Test
    void WhenDeleteTuskSubtuskEpic() {
        Task task = new Task("Task", "1");
        manager.addTask(task);
        Epic epic = new Epic("Epic", "2");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "3", epic.getId());
        manager.addSubTask(subTask);
        manager.deleteForId(task.getId());
        manager.deleteForId(epic.getId());
        assertNull(manager.getTask(task.getId()), "Задача не удалена");
        assertNull(manager.getEpic(epic.getId()), "Задача не удалена");
        assertNull(manager.getSubTask(subTask.getId()), "Задача не удалена");
    }


    @Test
    void whenUpdateTask() {
        Task task = new Task("Task", "Task");
        manager.addTask(task);
        Task task1 = new Task("Task2", "Task2", task.getId());
        manager.updateTask(task1);
        assertEquals(manager.getTask(task.getId()), task1, "Задачи не совпадают");
    }

    @Test
    void whenUpdateEpicSubtask() {
        Epic epic = new Epic("Эпик", "1");
        manager.addEpic(epic);
        Status epicStatus = epic.getStatus();
        SubTask subTask = new SubTask("Подзадача", "1", epic.getId());
        manager.addSubTask(subTask);
        SubTask subTask1 = new SubTask("Новая подзадача", "1", subTask.getId(), Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);
        manager.updateEpic(epic);
        assertEquals(manager.getSubTask(subTask.getId()), subTask1, "Задачи не совпадают");
        assertNotEquals(epicStatus, manager.getEpic(epic.getId()).getStatus(), "Статус не изменился");
    }
}