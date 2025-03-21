package projectTest;

import main.manager.FileBackedTaskManager;
import main.manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager getNewManager() {
        return null;
    }

    FileBackedTaskManager manager;
    Task task;
    Path backupFile;
    File tempFile;

    @BeforeEach
    void createFile() throws IOException {
        tempFile = File.createTempFile("test_tasks", ".csv");
        backupFile = tempFile.toPath(); // Получаем Path из File
        manager = new FileBackedTaskManager(backupFile);
        task = new Task("Task1", "1", "20.03.2024 17:20", 15);
    }


    @Test
    void saveTaskToFile() throws IOException {

        manager.addTask(task);
        assertTrue(Files.exists(backupFile), "Невозможно найти файл");
        assertNotEquals(0, Files.size(backupFile), "Файл пустой");
    }

    @Test
    void readFile() {
        manager.addTask(task);
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(backupFile);
        assertNotNull(manager2.getTask(1), "При чтении задачи выдаётся null");
        assertEquals(manager.getTask(1).getId(), manager2.getTask(1).getId(), "Задача не совпадает с сохранённой");
        assertEquals(manager.getTask(1).getName(), manager2.getTask(1).getName(), "Задача не совпадает с сохранённой");

    }


}
