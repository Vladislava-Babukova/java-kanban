package main.manager;

import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    private int idCustom = 0;


    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, SubTask> subtasks = new HashMap<>();

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
    public void addTask(Task task) {
        task.setId(checIDCustom());
        task.setStatus(Status.NEW);
        tasks.put(idCustom, task);
    }


    @Override
    public void addEpic(Epic epic) {
        epic.setId(checIDCustom());
        epic.setStatus(Status.NEW);
        epics.put(idCustom, epic);
    }

    @Override
    public Integer checIDCustom() {
        for (Integer id : tasks.keySet()) {
            if (idCustom < id) {
                idCustom = id;

            }
        }
        for (Integer id : epics.keySet()) {
            if (idCustom < id) {
                idCustom = id;

            }
        }
        for (Integer id : subtasks.keySet()) {
            if (idCustom < id) {
                idCustom = id;

            }
        }
        idCustom++;
        return idCustom;
    }

    @Override
    public void addSubTask(SubTask subTask) {

        subTask.setId(checIDCustom());
        subTask.setStatus(Status.NEW);
        for (Epic epicis : epics.values())
            if (epicis.getId() == subTask.getEpicId()) {
                subtasks.put(idCustom, subTask);
                Epic epic = epics.get(subTask.getEpicId());
                epic.getSubTasksinEpic().add(subTask.getId());
                epics.put(epic.getId(), epic);
            }
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
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
        epics.clear();
        subtasks.clear();

    }

    @Override
    public void deleteForId(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
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
                historyManager.remove(delSubTask);
            }
            epics.remove(id);
            historyManager.remove(id);

        }
        if (subtasks.containsKey(id)) {
            List<Integer> subTasksList = new ArrayList<>();
            for (Epic epic : epics.values()) {
                subTasksList = epic.getSubTasksinEpic();
                if (epic.getSubTasksinEpic().contains(id)) {
                    Integer i = id;
                    subTasksList.remove(i);
                    epic.setSubTasksList(subTasksList);
                    break;
                }
            }
            subtasks.remove(id);
            historyManager.remove(id);

        }
    }


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<SubTask> getSubtaskForEpic(Epic epic) {
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
