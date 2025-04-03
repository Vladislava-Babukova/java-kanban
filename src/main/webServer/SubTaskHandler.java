package main.webServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.manager.TaskManager;
import main.manager.model.SubTask;

import java.io.IOException;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    Gson gson;
    TaskManager manager;

    public SubTaskHandler(TaskManager manager) {
        this.manager = manager;
        gson = GsonBuilder.getGson();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String command = getBaseHandler(path, httpExchange.getRequestMethod());
            String response;
            String[] splitPath = path.split("/");
            int id = 0;

            switch (command) {
                case "get_subtasks_id":
                    try {
                        id = Integer.parseInt(splitPath[2]);

                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Некорректный формат id задачи");
                    }
                    if (manager.getSubTask(id) != null) {
                        response = gson.toJson(manager.getSubTask(id));
                        sendText(httpExchange, response);
                    } else {
                        sendNotFound(httpExchange, "Подзадача с id " + splitPath[2] + " не найдена");
                    }

                    break;
                case "get_subtasks":
                    if (manager.getAllSubTasks().isEmpty()) {
                        sendNotFound(httpExchange, "Список подзадач пуст");
                    } else {
                        response = gson.toJson(manager.getAllSubTasks());
                        sendText(httpExchange, response);
                    }
                    return;
                case "post_subtasks":
                    Optional<String> body = getBodyForHandler(httpExchange);
                    if (body.isEmpty()) {
                        sendNotFound(httpExchange, "Задача не передана в теле POST запроса");
                        break;
                    }
                    SubTask subtask = gson.fromJson(body.get(), SubTask.class);
                    if (subtask.getId() > 0) {
                        if (manager.getSubTask(subtask.getId()) != null) {
                            manager.updateSubTask(subtask);
                            if (manager.getSubTask(subtask.getId()) != null) {
                                sendSuccess(httpExchange, "Подзадача обновлена");
                            } else {
                                sendHasInteractions(httpExchange, "Подзадача пересекается по времени с существующими");
                            }
                        } else {
                            sendNotFound(httpExchange, "подзадачи с id " + subtask.getId() + " не существует.");
                        }
                    } else if ((subtask.getId() == 0)) {
                        if (manager.timeMatchCheck(subtask)) {
                            manager.addSubTask(subtask);
                            if (manager.getSubTask(subtask.getId()) != null) {
                                sendSuccess(httpExchange, "Задача добавлена");
                            } else {
                                sendNotFound(httpExchange, "Задача не добавилась");
                            }
                        } else {
                            sendHasInteractions(httpExchange, "Подзадача пересекается по времени с существующими");
                        }


                    } else {
                        sendBadRequest(httpExchange, "Некорректный id");
                    }
                    break;

                case "delete_subtasks_id":
                    try {
                        id = Integer.parseInt(splitPath[2]);

                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Некорректный формат id подзадачи");
                    }
                    if (manager.getSubTask(id) != null) {
                        manager.deleteForId(id);
                        sendText(httpExchange, "Подзадача удалена");
                    } else {
                        sendNotFound(httpExchange, "Подзадача c id " + id + "не найдена");
                    }
                    break;
                default:
                    sendBadRequest(httpExchange, "Некорректный запрос");

            }

        } catch (Exception e) {
            System.out.println(String.format("Возникла ошибка", e.getMessage()));
        }
    }


}
