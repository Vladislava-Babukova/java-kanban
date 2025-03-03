package main.manager;

import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;

import java.util.List;

public interface TaskManager {
    void epicStatus(Epic epic);

    void updateEpic(Epic newEpic);


    void updateTask(Task newtask);

    void updateSubTask(SubTask newSubTask);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void deleteAllTasks();

    void deleteForId(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    List<SubTask> getSubtaskForEpic(Epic epic);

    public List<Task> getHistory();

    public Integer checIDCustom();

}

