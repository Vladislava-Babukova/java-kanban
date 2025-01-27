package main.manager;

import main.manager.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class inMemoryHistoryManager implements HistoryManager<Task> {

    private final ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void addTaskInHistory(Task task) {
        if (Objects.isNull(task)) {
            return;
        }
        historyList.add(new Task(task.getName(), task.getDetails(), task.getId(), task.getStatus()));
        if (historyList.size() > 10) {
            historyList.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
