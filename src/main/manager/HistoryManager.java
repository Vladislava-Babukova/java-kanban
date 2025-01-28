package main.manager;

import main.manager.model.Task;

import java.util.List;

public interface HistoryManager {

    public void add(Task task);

    public List<Task> getHistory();


}
