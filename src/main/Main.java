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

        Task task = new Task("Почистить зубы", "иначе отвалятся");
        manager2.addTask(task);
        Task task1 = new Task("Запустить стирку", "белое");
        manager2.addTask(task1);
        Epic epic = new Epic("Сходить в магазин", "купить еды");
        manager2.addEpic(epic);
        SubTask subTask2 = new SubTask("Запустить посудомойку", "на режиме эко", epic.getId());
        manager2.addSubTask(subTask2);
        SubTask subTask1 = new SubTask("Протереть столы", "начисто", epic.getId());
        manager2.addSubTask(subTask1);
        SubTask subTask = new SubTask("составить список покупок", "не забыть", epic.getId());
        manager2.addSubTask(subTask);
        Epic epic1 = new Epic("Сходить на тренировку", "в 7 вечера");
        manager2.addEpic(epic1);


        System.out.println(manager2.getEpic(3));
        System.out.println(manager2.getTask(2));
        System.out.println(manager2.getSubTask(4));
        System.out.println(manager2.getSubTask(6));
        System.out.println(manager2.getSubTask(5));
        System.out.println(manager2.getEpic(7));
        System.out.println(manager2.getTask(1));
        System.out.println("Проверка вывода эпиков");
        for (SubTask subtask : manager2.getSubtaskForEpic(manager2.getEpic(3))) {
            System.out.println(subtask);
        }


    }
}
