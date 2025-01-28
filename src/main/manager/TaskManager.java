package main.manager;

import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;

import java.util.List;

public interface TaskManager {
    void epicStatus(Epic epic);

    void updateEpic(Epic newEpic);

    void epicStatus();

    void updateTask(Task newtask);

    void updateSubTask(SubTask newSubTask);

    Task addTask(Task task);

    Task addEpic(Epic epic);

    Task addSubTask(SubTask subTask);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void deleteAllTasks();

    void deleteForId(int id);

    List getAllTasks();

    List getAllEpics();

    List getAllSubTasks();

    List getSubtaskForEpic(Epic epic);

    public List<Task> getHistory();
}

