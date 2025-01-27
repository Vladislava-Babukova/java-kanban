package test.java;

import main.manager.HistoryManager;
import main.manager.Managers;
import main.manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void WhengetDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        assertNotNull(taskManager, "Объект не создан");
    }

    @Test
    void getDefaultHistoryTaskManager() {
        HistoryManager historyManager = Managers.getDefaultHistoryTaskManager();
        assertNotNull(historyManager, "Объект не создан");
    }
}