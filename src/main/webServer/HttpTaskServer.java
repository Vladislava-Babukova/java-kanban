package main.webServer;

import com.sun.net.httpserver.HttpServer;
import main.manager.Managers;
import main.manager.TaskManager;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private TaskManager manager;
    private HttpServer server;


    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress("localHost", PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubTaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));

    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getTaskManager();
        final HttpTaskServer taskServer = new HttpTaskServer(taskManager);

        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        taskManager.addTask(task);
        Epic epic = new Epic("Epic1", "Epic1");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        taskManager.addSubTask(subTask1);
        taskServer.start();

    }

    public void start() {
        System.out.println("Веб-сервер менеджера задач начал работу на порту " + PORT);
        this.server.start();
    }

    public void stop() {
        System.out.println("Веб-сервер менеджера задач закончил работу на порту " + PORT);
        this.server.stop(0);
    }

}
