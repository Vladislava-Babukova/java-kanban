package projectTest;

import main.manager.Managers;
import main.manager.Status;
import main.manager.TaskManager;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    TaskManager manager;

    protected abstract T getNewManager();

    @BeforeEach
    protected void getManager() {
        manager = Managers.getTaskManager();
    }

    @Test
    public void whenAddTask() {
        Task task = new Task("Task", "1");
        manager.addTask(task);

        final Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.getName(), savedTask.getName(), "Имя не совпадает");
        assertEquals(task.getDetails(), savedTask.getDetails(), "Описание не совпадает");
    }


    @Test
    public void whenAddEpic() {
        Epic epic = new Epic("Epic", "1");
        manager.addEpic(epic);
        final int epicId = epic.getId();

        final Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic.getName(), savedEpic.getName(), "Имя не совпадает");
        assertEquals(epic.getDetails(), savedEpic.getDetails(), "Описание не совпадает");
    }

    @Test
    public void whenAddSubTask() {
        Epic epic = new Epic("Epic", "1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "1", epic.getId(), "03.03.2024 11:11", 7);
        manager.addSubTask(subTask);
        final int subTaskId = subTask.getId();

        final SubTask savedSubTask = manager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

        final List<SubTask> subTasks = manager.getAllSubTasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask.getName(), savedSubTask.getName(), "Имя не совпадает");
        assertEquals(subTask.getDetails(), savedSubTask.getDetails(), "Описание не совпадает");
    }


    @Test
    public void whenAddEpicInSubTask() {
        Epic epic = new Epic("Epic", "1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "1", epic.getId(), epic.getId(), Status.NEW, "01.01.2024 17:17", 17);
        manager.addSubTask(subTask);
        int subtaskId = subTask.getId();
        int epicId = epic.getId();
        assertNotEquals(subtaskId, epicId, "ID совпадают");
    }

    @Test
    public void whenAddSubTaskInEpic() {
        SubTask subTask = new SubTask("subTask", "1", 20, 20, Status.IN_PROGRESS, "11.11.2024 11:20", 8);
        manager.addSubTask(subTask);
        Epic epic = new Epic("Epic", "1", 20);
        manager.addEpic(epic);
        int subtaskId = subTask.getId();
        int epicId = epic.getId();
        assertNotEquals(epicId, subtaskId, "ID совпадают");
    }

    @Test
    public void whenDeleteTuskSubtuskEpic() {
        Task task = new Task("Task", "1");
        manager.addTask(task);
        Epic epic = new Epic("Epic", "2");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "3", epic.getId(), "07.07.2024 14:07", 15);
        manager.addSubTask(subTask);
        manager.deleteForId(task.getId());
        manager.deleteForId(epic.getId());
        assertNull(manager.getTask(task.getId()), "Задача не удалена");
        assertNull(manager.getEpic(epic.getId()), "Задача не удалена");
        assertNull(manager.getSubTask(subTask.getId()), "Задача не удалена");
    }

    @Test
    public void subtaskShouldBeRemovedFromList() {
        Epic epic = new Epic("Сходить в магазин", "купить еды");
        manager.addEpic(epic);
        SubTask subTask2 = new SubTask("Запустить посудомойку", "на режиме эко", epic.getId());
        manager.addSubTask(subTask2);
        manager.deleteForId(subTask2.getId());
        List<Integer> subtasksInEpicAfter = epic.getSubTasksinEpic();
        assertFalse(subtasksInEpicAfter.contains(subTask2.getId()), "Список подзадач не должен содержать " +
                "subTask2.getId() после удаления");
    }

    @Test
    public void whenUpdateTask() {
        Task task = new Task("Task", "Task");
        manager.addTask(task);
        Task task1 = new Task("Task2", "Task2", task.getId());
        manager.updateTask(task1);
        assertEquals(manager.getTask(task.getId()), task1, "Задачи не совпадают");
    }

    @Test
    public void whenUpdateEpicSubtask() {
        Epic epic = new Epic("Эпик", "1");

        manager.addEpic(epic);
        Status epicStatus = epic.getStatus();
        SubTask subTask = new SubTask("Подзадача", "1", epic.getId());
        manager.addSubTask(subTask);
        SubTask subTask2 = new SubTask("Подзадача2", "2", epic.getId());
        manager.addSubTask(subTask2);
        SubTask subTask1 = new SubTask(subTask.getId(), "Новая подзадача", Status.IN_PROGRESS, "1", epic.getId());
        SubTask subTask11 = new SubTask(subTask.getId(), "Новая подзадача", Status.DONE, "1", epic.getId());
        SubTask subTask22 = new SubTask(subTask2.getId(), "Новая подзадача2", Status.DONE, "1", epic.getId());
        manager.updateEpic(epic);
        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus(), "Неверный статус эпика");
        manager.updateSubTask(subTask1);

        assertEquals(manager.getSubTask(subTask.getId()), subTask1, "Задачи не совпадают");
        assertEquals(epicStatus, manager.getEpic(epic.getId()).getStatus(), "Статус  изменился");

        manager.updateEpic(epic);
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus(), "Неверный статус эпика");

        manager.updateSubTask(subTask22);
        manager.updateEpic(epic);
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus(), "Неверный статус эпика");

        manager.updateSubTask(subTask11);
        manager.updateEpic(epic);
        assertEquals(Status.DONE, epic.getStatus(), "Неверный статус эпика");
    }
}


