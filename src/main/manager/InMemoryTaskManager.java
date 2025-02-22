package main.manager;

import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    private int idCusnom = 0;


    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();


    @Override
    public void epicStatus(Epic epic) {
        int newEpic = 0;
        int done = 0;
        int progress = 0;
        Status status = null;
        for (Integer id : epic.getSubTasksinEpic()) {

            for (Integer i : subtasks.keySet()) {
                if (i == id) {
                    status = getSubTask(i).getStatus();
                }
            }
            if (status == Status.NEW) {
                ++newEpic;
            }
            if (status == Status.IN_PROGRESS) {
                ++progress;
            }
            if (status == Status.DONE) {
                ++done;
            }

        }
        if (((done == 0) && (progress == 0))) {
            epic.setStatus(Status.NEW);
        } else if ((newEpic == 0) && (progress == 0) && (done > 0)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    @Override
    public void updateEpic(Epic newEpic) {
        if (epics.containsKey(newEpic.getId())) {
            epicStatus(newEpic);
            epics.put(newEpic.getId(), newEpic);

        }
    }

    @Override
    public void epicStatus() {

    }


    @Override
    public void updateTask(Task newtask) {
        if (tasks.containsKey(newtask.getId())) {
            tasks.put(newtask.getId(), newtask);
        }
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        if (subtasks.containsKey(newSubTask.getId())) {
            subtasks.put(newSubTask.getId(), newSubTask);
        }
    }


    @Override
    public Task addTask(Task task) {
        task.setId(++idCusnom);
        task.setStatus(Status.NEW);
        tasks.put(idCusnom, task);
        return task;
    }


    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(++idCusnom);
        epic.setStatus(Status.NEW);
        epics.put(idCusnom, epic);
        return epic;
    }


    @Override
    public SubTask addSubTask(SubTask subTask) {
        subTask.setId(++idCusnom);
        subTask.setStatus(Status.NEW);
        for (Epic epicis : epics.values())
            if (epicis.getId() == subTask.getEpicId()) {
                subtasks.put(idCusnom, subTask);
                Epic epic = epics.get(subTask.getEpicId());
                epic.getSubTasksinEpic().add(subTask.getId());
                epics.put(epic.getId(), epic);
            }
        return subTask;
    }


    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subtasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteForId(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
        if (epics.containsKey(id)) {
            for (Integer i : epics.get(id).getSubTasksinEpic()) {
                int delSubTask = 0;
                for (Integer integer : subtasks.keySet()) {
                    if (integer == i) {
                        delSubTask = integer;
                    }
                }
                subtasks.remove(delSubTask);
            }
            epics.remove(id);

        }
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);

        }
    }


    @Override
    public List getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List getSubtaskForEpic(Epic epic) {
        List<SubTask> subTasksList = new ArrayList<>();
        for (Integer subTaskId : epic.getSubTasksinEpic()) {
            for (Integer i : subtasks.keySet()) {
                if (i == subTaskId) {
                    subTasksList.add(subtasks.get(i));
                }
            }
        }
        return subTasksList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
