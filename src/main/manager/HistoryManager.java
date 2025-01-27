package main.manager;

import main.manager.model.Task;

import java.util.List;

public interface HistoryManager<T extends Task> {

    public void addTaskInHistory(Task task);

    public List<Task> getHistory();


}
