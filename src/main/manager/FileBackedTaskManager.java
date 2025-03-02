package main.manager;

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
        if (task.getClass() == Task.class) {
            type = TypeTask.TASK;
        } else if (task.getClass() == Epic.class) {
            type = TypeTask.EPIC;
        } else {
            type = TypeTask.SUBTASK;
        }
        string = task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + "," + task.getDetails();

        if (type == TypeTask.SUBTASK) {
            string = string + "," + ((SubTask) task).getEpicId();
        }
        return string;
    }

    void save() throws ManagerSaveException {
        List<Task> tasks = getAllTasks();
        List<Epic> epics = getAllEpics();
        List<SubTask> subTasks = getAllSubTasks();

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(backupFile.toString()))) {
            fileWriter.write("Id,type,name,status,details,epic_id" + "\n");
            if (!(tasks == null)) {
                for (Task task : tasks) {
                    fileWriter.write(toString(task) + "\n");
                }
            }

            if (!(epics == null)) {
                for (Epic epic : epics) {
                    fileWriter.write(toString(epic) + "\n");
                }
            }
            if (!(subTasks == null)) {
                for (SubTask subTask : subTasks) {
                    fileWriter.write(toString(subTask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно работать с файлом");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path backupFile) {
        final FileBackedTaskManager result = new FileBackedTaskManager();
        String back = "";
        try {
            back = Files.readString(Path.of(backupFile.toString()));
        } catch (IOException exception) {
            System.out.println("Ошибка чтения файла");
        }
        String[] tasks = back.split("\n");
        for (int i = 1; i < tasks.length; i++) {
            if (tasks[i].contains(TypeTask.EPIC.toString())) {
                try {
                    Epic epic = (Epic) fromString(tasks[i]);
                    result.epics.put(epic.getId(), epic);
                } catch (NullPointerException e) {
                    System.out.println("Эпик вернулся как null");
                }
            } else if (tasks[i].contains(TypeTask.SUBTASK.toString())) {
                try {
                    SubTask subTask = (SubTask) fromString(tasks[i]);
                    result.subtasks.put(subTask.getId(), subTask);
                    Epic epic;
                    epic = result.epics.get(subTask.getEpicId());
                    if (!(epic == null)) {
                        List<Integer> subtasksList = epic.getSubTasksinEpic();
                        subtasksList.add(subTask.getId());
                        epic.setSubTasksList(subtasksList);
                        result.updateEpic(epic);
                    } else {
                        System.out.println("Не получилось привязаться к Эпикам");
                    }
                } catch (NullPointerException e) {
                    System.out.println("Подзадача вернулась как null");
                }
            } else {
                try {
                    Task task = fromString(tasks[i]);
                    result.tasks.put(task.getId(), task);
                } catch (NullPointerException e) {
                    System.out.println("Задача вернулась как null");
                }
            }
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

        if (value.contains(TypeTask.EPIC.toString())) {
            return new Epic(id, taskValue[2], status, taskValue[4]);
        } else if (value.contains(TypeTask.SUBTASK.toString())) {
            int epicId = Integer.parseInt(taskValue[5]);

            return new SubTask(taskValue[2], taskValue[4], epicId, id, status);
        } else if (value.contains(TypeTask.TASK.toString())) {
            return new Task(id, taskValue[2], status, taskValue[4]);
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
        return super.getSubtaskForEpic(epic);
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

