package projectTest;

import com.google.gson.Gson;
import main.manager.Managers;
import main.manager.Status;
import main.manager.TaskManager;
import main.manager.model.Epic;
import main.manager.model.SubTask;
import main.manager.model.Task;
import main.webServer.GsonBuilder;
import main.webServer.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {
    private HttpTaskServer server;
    private TaskManager manager;
    private Gson gson;


    @BeforeEach
    void init() throws IOException {
        manager = Managers.getTaskManager();
        server = new HttpTaskServer(manager);
        gson = GsonBuilder.getGson();
        server.start();

    }

    @AfterEach
    void stop() {
        manager.deleteAllTasks();
        server.stop();
    }


    @Test
    void createTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        String jsonTask = gson.toJson(task);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Количество задач не совпадает");
    }

    @Test
    void createEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        String jsonTask = gson.toJson(epic);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size(), "Количество эпиков не совпадает");
    }

    @Test
    void createSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        String jsonTask = gson.toJson(subTask1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size(), "Количество задач не совпадает");
        assertEquals(1, manager.getSubtaskForEpic(epic).size(), "Количество подзадач внутри эпика не совпадает");
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        Task task1 = new Task("Task1", "Task1", 1, Status.IN_PROGRESS, "11.03.2024 10:20", 10L);
        String jsonTask = gson.toJson(task1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Количество задач не совпадает");
        assertEquals(Status.IN_PROGRESS, manager.getTask(1).getStatus(), "Статус не совпадает");
    }

    @Test
    void updateSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);
        SubTask subTask1 = new SubTask("Subtask1", "subtask1", epic.getId(), 2, Status.IN_PROGRESS, "11.03.2024 22:22", 17);
        String jsonTask = gson.toJson(subTask1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size(), "Количество задач не совпадает");
        assertEquals(Status.IN_PROGRESS, manager.getSubTask(2).getStatus(), "Статус не совпадает");
        assertEquals(Status.IN_PROGRESS, manager.getEpic(1).getStatus(), "Статус эпика не совпадает");
    }

    @Test
    void getTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        String jsonTask = gson.toJson(task);


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getAllTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String jsonTasks = gson.toJson(manager.getAllTasks());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");
        assertEquals(jsonTasks, response.body(), "Список вернулся неверно");
    }

    @Test
    void getSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);
        String jsonTask = gson.toJson(subTask);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/2");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getAllSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);
        String jsonTask = gson.toJson(manager.getAllSubTasks());

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        String jsonTask = gson.toJson(epic);


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/1");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllEpics().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getAllEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        String jsonTask = gson.toJson(manager.getAllEpics());


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllEpics().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getSubtaskInEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);
        String jsonTask = gson.toJson(manager.getSubtaskForEpic(epic));


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/1/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getSubtaskForEpic(epic).size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        manager.getTask(task.getId());
        String jsonTask = gson.toJson(manager.getHistory());


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/history");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getHistory().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }

    @Test
    void getPrioritizedTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        String jsonTask = gson.toJson(manager.getPrioritizedTasks());


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/prioritized");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getPrioritizedTasks().size(), "Неверное количество задач");
        assertEquals(jsonTask, response.body(), "Задача вернулась неверно");
    }


    @Test
    void deleteTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .DELETE().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач не пуст");
    }

    @Test
    void deleteSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/2");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .DELETE().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubTasks().isEmpty(), "Список подзадач не пуст");
        assertTrue(manager.getSubtaskForEpic(epic).isEmpty(), "Список подзадач в эпике не пуст");
    }

    @Test
    void deleteEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/1");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .DELETE().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubTasks().isEmpty(), "Список подзадач не пуст");
        assertTrue(manager.getAllEpics().isEmpty(), "Список подзадач в эпике не пуст");
    }

    @Test
    void createTimeIntersectionTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        Task task1 = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        String jsonTask = gson.toJson(task1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Количество задач не совпадает");
    }

    @Test
    void getNotExistentTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Task1", "11.03.2024 10:20", 10L);
        manager.addTask(task);
        String jsonTask = gson.toJson(task);


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/2");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач");

    }

    @Test
    void getNotExistentEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/2");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(1, manager.getAllEpics().size(), "Неверное количество задач");

    }

    @Test
    void getNotExistentSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);


        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtask/1");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size(), "Неверное количество задач");

    }

    @Test
    void getSubtaskInNotExistentEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/2/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void createTimeIntersectionSubTaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        manager.addSubTask(subTask);
        SubTask subTask1 = new SubTask("Subtask1", "subtask1", epic.getId(), "11.03.2024 22:22", 17);
        String jsonTask = gson.toJson(subTask1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size(), "Количество задач не совпадает");
    }
}



