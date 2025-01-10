public class SubTask extends Task {
    int epicId;

    public SubTask(String name, String details, int epicId) {
        super(name, details);
        this.epicId = epicId;
    }

    public SubTask(String name, String details, int id, Status status) {
        super(name, details, id, status);
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}

