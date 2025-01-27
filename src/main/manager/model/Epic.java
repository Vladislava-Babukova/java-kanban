package main.manager.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksList = new ArrayList<>();

    public Epic(String name, String details) {
        super(name, details);
    }

    public Epic(String name, String details, int id) {
        super(name, details, id);
    }

    public ArrayList<Integer> getSubTasksinEpic() {
        return subTasksList;
    }
}


