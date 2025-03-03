package main.manager.model;

import main.manager.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasksList = new ArrayList<>();

    public Epic(String name, String details) {
        super(name, details);
    }

    public Epic(int id, String name, Status status, String details) {
        super(id, name, status, details);
    }

    public Epic(String name, String details, int id) {
        super(name, details, id);
    }

    public List<Integer> getSubTasksinEpic() {
        return subTasksList;
    }

    public void setSubTasksList(List<Integer> subTasksList) {
        this.subTasksList = subTasksList;
    }
}


