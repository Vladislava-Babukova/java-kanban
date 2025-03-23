package projectTest;

import main.manager.FileBackedTaskManager;
import main.manager.HistoryManager;
import main.manager.Managers;
import main.manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void whenGetDefaultTaskManager() {
        TaskManager taskManager = Managers.getTaskManager();
        assertNotNull(taskManager, "Объект не создан");
    }

    @Test
    void getDefaultHistoryTaskManager() {
        HistoryManager historyManager = Managers.getDefaultHistoryManager();
        assertNotNull(historyManager, "Объект не создан");
    }

    @Test
    void getDefaultFileBackedTaskManager() {
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager();
        assertNotNull(fileBackedTaskManager, "Объект не создан");
    }
}