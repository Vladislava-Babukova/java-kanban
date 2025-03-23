package main.manager.model;

import main.manager.Status;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String details, int epicId, String startTime, int duration) {
        super(name, details, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String details, int epicId) {
        super(name, details);
        this.epicId = epicId;
    }

    public SubTask(String name, String details, int epicId, int id, Status status, String startTime, long duration) {
        super(name, details, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, Status status, String details, int epicId) {
        super(id, name, status, details);
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}

