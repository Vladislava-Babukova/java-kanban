public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task = new Task("Почистить зубы", "иначе отвалятся");
        taskManager.addTask(task);
        Task task1 = new Task("Запустить стирку", "белое");
        taskManager.addTask(task1);
        System.out.println(taskManager.getAllTusks());

        Epic epic = new Epic("Сходить в магазин", "купить еды");
        taskManager.addEpic(epic);
        Epic epic1 = new Epic("Уборка дома", "на кухне");
        taskManager.addEpic(epic1);


        SubTask subTask = new SubTask("составить список покупок", "не забыть", epic.getId());
        taskManager.addSubTask(subTask);
        SubTask subTask1 = new SubTask("Протереть столы", "начисто", epic1.getId());
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Запустить посудомойку", "на режиме эко", epic1.getId());
        taskManager.addSubTask(subTask2);

        System.out.println(taskManager.getEpic(epic.getId()));
        System.out.println(taskManager.getSubtaskForEpic(epic));
        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println(taskManager.getSubtaskForEpic(epic1));

        SubTask newSubTask1 = new SubTask("Протереть столы", "начисто", 6, Status.DONE);
        taskManager.updateSubTask(newSubTask1);
        taskManager.updateEpic(epic1);
        Task newtask1 = new Task("Развесить стирку", "белое", 2, Status.IN_PROGRESS);
        taskManager.updateTask(newtask1);
        taskManager.deleteForId(7);
        taskManager.deleteForId(3);

        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println(taskManager.getSubtaskForEpic(epic1));
        System.out.println(taskManager.getAllTusks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTusks());

    }
}