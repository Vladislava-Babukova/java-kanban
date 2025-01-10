

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    private int idCusnom = 0;


    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subtasks = new HashMap<>();


    public void updateEpic(Epic newEpic) {
        if (epics.containsKey(newEpic.getId())) {
            epicStatus(newEpic);
            epics.put(newEpic.getId(), newEpic);

        }
    }

    private void epicStatus(Epic epic) {
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


    public void updateTask(Task newtask) {
        if (tasks.containsKey(newtask.getId())) {
            tasks.put(newtask.getId(), newtask);
        }
    }

    public void updateSubTask(SubTask newSubTask) {
        if (subtasks.containsKey(newSubTask.getId())) {
            subtasks.put(newSubTask.getId(), newSubTask);
        }
    }


    public Task addTask(Task task) {
        task.setId(++idCusnom);
        task.setStatus(Status.NEW);
        tasks.put(idCusnom, task);
        return task;
    }


    public Task addEpic(Epic epic) {
        epic.setId(++idCusnom);
        epic.setStatus(Status.NEW);
        epics.put(idCusnom, epic);
        return epic;
    }


    public Task addSubTask(SubTask subTask) {

        subTask.setId(++idCusnom);
        subTask.setStatus(Status.NEW);
        subtasks.put(idCusnom, subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTasksinEpic().add(subTask.getId());
        epics.put(epic.getId(), epic);
        return subTask;
    }


    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {
        return subtasks.get(id);
    }

    public void deleteAllTascs() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

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


    public ArrayList getAllTusks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList getAllSubTusks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList getSubtaskForEpic(Epic epic) {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (Integer subTaskId : epic.getSubTasksinEpic()) {
            for (Integer i : subtasks.keySet()) {
                if (i == subTaskId) {
                    subTasksList.add(subtasks.get(i));
                }
            }
        }
        return subTasksList;
    }
}
