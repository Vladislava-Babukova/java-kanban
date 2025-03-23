package main;

import main.manager.FileBackedTaskManager;
import main.manager.Managers;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    private static final String FILE_NAME = "src/resources/backup.csv";

    public static void main(String[] args) throws IOException {
        Path backupFile;
        FileBackedTaskManager manager = Managers.getFileBackedTaskManager();
        backupFile = manager.createFileExistence();


        printInPreviousSprint(backupFile);


    }


    private static void printInPreviousSprint(Path backupFile) {
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(backupFile);

        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager2.addTask(task);
        Task task1 = new Task("Task2", "Task2", "11.03.2024 11:23", 40L);
        manager2.addTask(task1);
        Epic epic = new Epic("Epic1", "Epic1");
        manager2.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager2.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("subtask2", "subtask2", epic.getId(), "11.03.2024 13:43", 25);
        manager2.addSubTask(subTask2);


        System.out.println(manager2.getEpic(3));
        System.out.println(manager2.getTask(2));
        System.out.println(manager2.getSubTask(4));

        System.out.println(manager2.getSubTask(5));

        System.out.println(manager2.getTask(1));
        System.out.println("Проверка вывода эпиков");
        for (SubTask subtask : manager2.getSubtaskForEpic(manager2.getEpic(3))) {
            System.out.println(subtask);
        }

        System.out.println("Проверка вывода по времени");
        for (Object taskInTime : manager2.getPrioritizedTasks()) {
            System.out.println(taskInTime.toString());
        }

    }
}
