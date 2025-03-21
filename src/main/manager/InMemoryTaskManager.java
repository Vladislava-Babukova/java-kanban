package main.manager;

import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private int idCustom = 0;

    protected static TreeMap<LocalDateTime, Object> taskTreeMap = new TreeMap<>(new StartTimeComparator());
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, SubTask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    public void timeMatchCheck(Task task) {

        if (task != null) {
            if (task.getStartTime() != null && task.getDuration() != null) {
                LocalDateTime startTime = task.getStartTime();
                LocalDateTime endTime = task.getEndTime();
                Boolean isFloorkey;
                Boolean isCeilingkey;
                if (taskTreeMap.floorKey(startTime) == null) {
                    isFloorkey = true;
                } else {
                    Task task2 = (Task) taskTreeMap.get(taskTreeMap.floorKey(startTime));
                    if (startTime.isAfter(task2.getEndTime())) {
                        isFloorkey = true;
                    } else {
                        isFloorkey = false;
                    }
                }
                if (taskTreeMap.ceilingKey(endTime) == null) {
                    isCeilingkey = true;
                } else if (endTime.isBefore(taskTreeMap.ceilingKey(endTime))) {
                    isCeilingkey = true;
                } else {
                    isCeilingkey = false;
                }
                if (isCeilingkey && isFloorkey) {
                    taskTreeMap.put(task.getStartTime(), task);
                }
            }
        }
    }

    public List<Object> getPrioritizedTasks() {
        List<Object> prioritizedTasks = taskTreeMap.values().stream().collect(Collectors.toUnmodifiableList());
        return prioritizedTasks;
    }

    public Epic detectEpicTime(Epic epic) {

        System.out.println("метод вызван");
        LocalDateTime newStartTime = epic.getSubTasksinEpic().stream()
                .map(id -> subtasks.get(id))
                .filter(subTask -> subTask != null && subTask.getStartTime() != null)
                .map(Task::getStartTime)
                .min(Comparator.naturalOrder())
                .orElse(null);
        System.out.println("newStartTime" + newStartTime);
        epic.setStartTime(newStartTime);
        System.out.println("Вывод времени эпика" + epic.getStartTime());
        LocalDateTime newEndTime = epic.getSubTasksinEpic().stream()
                .map(id -> subtasks.get(id))
                .filter(subTask -> subTask != null && subTask.getEndTime() != null)
                .map(Task::getEndTime)
                .min(Comparator.naturalOrder())
                .orElse(null);
        epic.setEndTime(newEndTime);


        return epic;
    }

    public Epic detectEpicDuration(Epic epic) {
        Duration duration = epic.getSubTasksinEpic().stream()
                .map(id -> subtasks.get(id))
                .filter(subTask -> subTask != null && subTask.getDuration() != null)
                .map(subTask -> subTask.getDuration())
                .reduce(Duration.ZERO, (d1, d2) -> d1.plus(d2));
        epic.setDuration(duration);
        return epic;
    }

    @Override
    public void epicStatus(Epic epic) {
        if (epic == null || epic.getSubTasksinEpic() == null || epic.getSubTasksinEpic().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Map<Status, Long> statusCounts = epic.getSubTasksinEpic().stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .filter(subtask -> subtask != null)
                .map(SubTask::getStatus)
                .collect(Collectors.groupingBy(status -> status, Collectors.counting()));

        long newCount = statusCounts.getOrDefault(Status.NEW, 0L);
        long doneCount = statusCounts.getOrDefault(Status.DONE, 0L);
        long progressCount = statusCounts.getOrDefault(Status.IN_PROGRESS, 0L);
        long totalSubtasks = epic.getSubTasksinEpic().size();

        if (doneCount == totalSubtasks) {
            epic.setStatus(Status.DONE);
        } else if (newCount == totalSubtasks || totalSubtasks == 0) {
            epic.setStatus(Status.NEW);
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
            if (newtask.getStartTime() != null) {
                taskTreeMap.remove(tasks.get(newtask.getId()).getStartTime());
                timeMatchCheck(newtask);
            }
            tasks.put(newtask.getId(), newtask);

        }
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        if (newSubTask != null) {
            if (subtasks.containsKey(newSubTask.getId())) {
                if (newSubTask.getStartTime() != null) {
                    taskTreeMap.remove(subtasks.get(newSubTask.getId()).getStartTime());
                    timeMatchCheck(newSubTask);
                }
                subtasks.put(newSubTask.getId(), newSubTask);
            }
            detectEpicTime(epics.get(newSubTask.getEpicId()));
        }
    }


    @Override
    public void addTask(Task task) {
        task.setId(checIDCustom());
        task.setStatus(Status.NEW);
        tasks.put(idCustom, task);
        timeMatchCheck(task);
    }


    @Override
    public void addEpic(Epic epic) {
        epic.setId(checIDCustom());
        epic.setStatus(Status.NEW);
        epics.put(idCustom, epic);
    }

    @Override
    public Integer checIDCustom() {
        tasks.keySet().forEach(id -> {
            if (idCustom < id) {
                idCustom = id;

            }
        });

        epics.keySet().forEach(id -> {
            if (idCustom < id) {
                idCustom = id;

            }
        });

        subtasks.keySet().forEach(id -> {
            if (idCustom < id) {
                idCustom = id;

            }
        });

        idCustom++;
        return idCustom;
    }

    @Override
    public void addSubTask(SubTask subTask) {

        subTask.setId(checIDCustom());
        subTask.setStatus(Status.NEW);
        epics.keySet().forEach(idEpic -> {
            if (idEpic == subTask.getEpicId()) {
                subtasks.put(idCustom, subTask);
                timeMatchCheck(subTask);
                Epic epic = epics.get(idEpic);
                List<Integer> listIdSubtasks = epic.getSubTasksinEpic();
                listIdSubtasks.add(subTask.getId());
                epic.setSubTasksList(listIdSubtasks);
                timeMatchCheck(subTask);
                epic = detectEpicTime(epic);
                epic = detectEpicDuration(epic);
                epics.put(epic.getId(), epic);
            }
        });

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

        tasks.keySet().forEach(historyManager::remove);
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        tasks.clear();
        epics.clear();
        subtasks.clear();
        taskTreeMap.clear();

    }

    @Override
    public void deleteForId(int id) {
        if (tasks.containsKey(id)) {
            if (tasks.get(id) != null && tasks.get(id).getStartTime() != null) {
                taskTreeMap.remove(tasks.get(id).getStartTime());
            }
            tasks.remove(id);
            historyManager.remove(id);
        }
        if (epics.containsKey(id)) {

            epics.get(id).getSubTasksinEpic().forEach(subtaskId -> {
                if (subtasks.get(subtaskId) != null && subtasks.get(subtaskId).getStartTime() != null) {
                    taskTreeMap.remove(subtasks.get(subtaskId).getStartTime());
                }
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            epics.remove(id);
            historyManager.remove(id);

        }
        if (subtasks.containsKey(id)) {
            List<Integer> subtaskList = (epics.get(subtasks.get(id).getEpicId()).getSubTasksinEpic());
            subtaskList.remove(Integer.valueOf(id));
            epics.get(subtasks.get(id).getEpicId()).setSubTasksList(subtaskList);
            if (subtasks.get(id) != null && subtasks.get(id).getStartTime() != null) {
                taskTreeMap.remove(subtasks.get(id).getStartTime());
                detectEpicTime(epics.get(subtasks.get(id).getEpicId()));
            }
            historyManager.remove(id);
            subtasks.remove(id);
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
        if (epic != null) {
            List<SubTask> subtaskList = epic.getSubTasksinEpic().stream()
                    .map(subtaskId -> subtasks.get(subtaskId))
                    .toList();
            return subtaskList;
        } else {
            return null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
