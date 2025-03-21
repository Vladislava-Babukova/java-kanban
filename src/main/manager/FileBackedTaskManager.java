package main.manager;

import exceptions.ManagerLoadExeption;
import exceptions.ManagerSaveException;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;
import main.manager.model.TypeTask;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.Duration;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String FILE_NAME = "src/resources/backup.csv";
    Path backupFile = Paths.get(FILE_NAME);

    public FileBackedTaskManager() {
        super();
    }

    public FileBackedTaskManager(Path backupFile) {
        super();
        this.backupFile = backupFile;
    }


    public Path createFileExistence() {
        if (!Files.exists(Path.of(FILE_NAME))) {
            try {
                backupFile = Files.createFile(Path.of(FILE_NAME));
            } catch (IOException e) {
                throw new ManagerSaveException("Файл не получилось создать");
            }
        }
        return backupFile;
    }


    public String toString(Task task) {
        TypeTask type;
        String string;
        String stringTime;
        if (task.getClass() == Task.class) {
            type = TypeTask.TASK;
        } else if (task.getClass() == Epic.class) {
            type = TypeTask.EPIC;
        } else {
            type = TypeTask.SUBTASK;
        }
        string = task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + "," + task.getDetails();
        stringTime = task.getStartTimeInString() + "," + task.getDuration();
        if (type == TypeTask.TASK) {
            string = string + "," + stringTime;
        }
        if (type == TypeTask.SUBTASK) {
            string = string + "," + stringTime + "," + ((SubTask) task).getEpicId();
        }
        return string;
    }

    void save() {
        List<Task> tasks = getAllTasks();
        List<Epic> epics = getAllEpics();
        List<SubTask> subTasks = getAllSubTasks();

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(backupFile.toString()))) {
            fileWriter.write("Id,type,name,status,details,startTime,duration,epic_id" + "\n");
            if (!(tasks == null)) {

                tasks.forEach(task -> {
                    try {
                        fileWriter.write(toString(task) + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            if (!(epics == null)) {

                epics.forEach(epic -> {
                    try {
                        fileWriter.write(toString(epic) + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            if (!(subTasks == null)) {

                subtasks.values().forEach(st -> {
                    try {
                        fileWriter.write(toString(st) + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно работать с файлом");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path backupFile) {
        final FileBackedTaskManager result = new FileBackedTaskManager();

        try {

            Files.lines(backupFile)
                    .skip(1)
                    .forEach(line -> {
                        if (line.contains(TypeTask.EPIC.toString())) {
                            try {
                                Epic epic = (Epic) fromString(line);
                                result.epics.put(epic.getId(), epic);
                            } catch (NullPointerException e) {
                                System.out.println("Эпик вернулся как null");
                            }
                        } else if (line.contains(TypeTask.SUBTASK.toString())) {
                            SubTask subTask = (SubTask) fromString(line);
                            if (subTask != null) {
                                result.subtasks.put(subTask.getId(), subTask);
                                taskTreeMap.put(subTask.getStartTime(), subTask);
                                Epic epic = result.epics.get(subTask.getEpicId());
                                if (epic != null) {
                                    List<Integer> subtasksList = epic.getSubTasksinEpic();
                                    subtasksList.add(subTask.getId());
                                    epic.setSubTasksList(subtasksList);
                                    result.updateEpic(epic);
                                } else {
                                    throw new ManagerLoadExeption("Ошибка привязки к эпикам");
                                }
                            }

                        } else {
                            try {
                                Task task = fromString(line);
                                result.tasks.put(task.getId(), task);
                                taskTreeMap.put(task.getStartTime(), task);
                            } catch (NullPointerException e) {
                                System.out.println("Задача вернулась как null");
                            }
                        }
                    });

        } catch (IOException exception) {
            throw new ManagerLoadExeption("Ошибка чтения файла");
        }
        return result;
    }

    public static Task fromString(String value) {
        Status status = Status.NEW;
        String[] taskValue = value.split(",");

        if (taskValue[3].equals(Status.IN_PROGRESS.toString())) {
            status = Status.IN_PROGRESS;
        } else if (taskValue[3].equals(Status.DONE.toString())) {
            status = Status.DONE;
        }

        int id = Integer.parseInt(taskValue[0]);
        long duration;
        if (value.contains(TypeTask.EPIC.toString())) {
            return new Epic(id, taskValue[2], status, taskValue[4]);
        } else if (value.contains(TypeTask.SUBTASK.toString())) {
            int epicId;
            if (taskValue.length > 6) {
                epicId = Integer.parseInt(taskValue[7]);
                Duration duration1 = Duration.parse(taskValue[6]);
                duration = duration1.toMinutes();
                return new SubTask(taskValue[2], taskValue[4], epicId, id, status, taskValue[5], duration);
            } else {
                epicId = Integer.parseInt(taskValue[5]);
                return new SubTask(id, taskValue[2], status, taskValue[4], epicId);

            }
        } else if (value.contains(TypeTask.TASK.toString())) {

            if (taskValue[6] == null || taskValue[6].isEmpty() || taskValue[6].equals("null")) {
                return new Task(id, taskValue[2], status, taskValue[4]);
            } else {
                try {
                    Duration duration1 = Duration.parse(taskValue[6]);
                    duration = duration1.toMinutes();
                    return new Task(taskValue[2], taskValue[4], id, status, taskValue[5], duration);

                } catch (java.time.format.DateTimeParseException e) {

                    new DateTimeException("неверный формат продолжительности");

                    return null;
                }

            }


        }
        return null;
    }

    @Override
    public void epicStatus(Epic epic) {
        super.epicStatus(epic);
    }


    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }


    @Override
    public void updateTask(Task newtask) {
        super.updateTask(newtask);
        save();
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        super.updateSubTask(newSubTask);
        save();
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }


    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }


    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }


    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        return subTask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteForId(int id) {
        super.deleteForId(id);
        save();
    }


    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public List<SubTask> getSubtaskForEpic(Epic epic) {
        if (epic != null) {
            return super.getSubtaskForEpic(epic);
        } else {
            return null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public Integer checIDCustom() {
        return super.checIDCustom();
    }
}

