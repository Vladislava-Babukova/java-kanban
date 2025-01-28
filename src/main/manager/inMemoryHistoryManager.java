package main.manager;

import main.manager.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class inMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>();
    static final int historySize = 10;

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }
        historyList.add(new Task(task.getName(), task.getDetails(), task.getId(), task.getStatus()));
        if (historyList.size() > historySize) {
            historyList.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
