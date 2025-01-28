package main;

import main.manager.Managers;
import main.manager.Status;
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
        Epic epic1 = new Epic("Уборка дома", "на кухне");
        manager.addEpic(epic1);


        SubTask subTask = new SubTask("составить список покупок", "не забыть", epic.getId());
        manager.addSubTask(subTask);
        SubTask subTask1 = new SubTask("Протереть столы", "начисто", epic1.getId());
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Запустить посудомойку", "на режиме эко", epic1.getId());
        manager.addSubTask(subTask2);

        System.out.println(manager.getTask(2));
        System.out.println(manager.getEpic(4));
        System.out.println(manager.getSubTask(5));
        printAllTasks(manager);

        Task newtask1 = new Task("Развесить стирку", "белое", 2, Status.IN_PROGRESS);
        manager.updateTask(newtask1);
        manager.deleteForId(7);
        manager.deleteForId(3);
        SubTask newSubTask1 = new SubTask("Протереть столы", "начисто", 6, Status.DONE);
        manager.updateSubTask(newSubTask1);
        manager.updateEpic(epic1);
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Object task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Object epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Object subTask : manager.getSubtaskForEpic((Epic) epic)) {
                System.out.println("--> " + subTask);
            }
        }

        System.out.println("Подзадачи:");
        for (Object subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
