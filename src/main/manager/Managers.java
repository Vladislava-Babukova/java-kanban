package main.manager;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {
        return new FileBackedTaskManager();
    }


    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

}
