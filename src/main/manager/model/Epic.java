package main.manager.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasksList = new ArrayList<>();

    public Epic(String name, String details) {
        super(name, details);
    }

    public Epic(String name, String details, int id) {
        super(name, details, id);
    }

    public List<Integer> getSubTasksinEpic() {
        return subTasksList;
    }
}


