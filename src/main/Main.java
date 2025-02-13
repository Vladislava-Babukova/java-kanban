package main;

import main.manager.Managers;
import main.manager.TaskManager;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;

public class Main {


    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultTaskManager();

        Task task = new Task("Почистить зубы", "иначе отвалятся");
        manager.addTask(task);
        Task task1 = new Task("Запустить стирку", "белое");
        manager.addTask(task1);
        Epic epic = new Epic("Сходить в магазин", "купить еды");
        manager.addEpic(epic);
        SubTask subTask2 = new SubTask("Запустить посудомойку", "на режиме эко", epic.getId());
        manager.addSubTask(subTask2);
        SubTask subTask1 = new SubTask("Протереть столы", "начисто", epic.getId());
        manager.addSubTask(subTask1);
        SubTask subTask = new SubTask("составить список покупок", "не забыть", epic.getId());
        manager.addSubTask(subTask);
        Epic epic1 = new Epic("Сходить на тренировку", "в 7 вечера");
        manager.addEpic(epic1);


        System.out.println(manager.getEpic(3));
        System.out.println(manager.getTask(2));
        System.out.println(manager.getTask(2));
        System.out.println(manager.getSubTask(4));
        System.out.println(manager.getSubTask(6));
        System.out.println(manager.getSubTask(5));
        System.out.println(manager.getEpic(7));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getTask(1));
        printHistory(manager);
        manager.deleteForId(3);
        printHistory(manager);
        manager.deleteForId(2);
        printHistory(manager);

    }

    private static void printHistory(TaskManager manager) {

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
