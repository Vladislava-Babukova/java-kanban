package main.manager.model;

import main.manager.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Task {
    private String name;
    private String details;
    private int id;
    private Status status;
    private TypeTask type;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String details, String startTime, long duration) {
        this.name = name;
        this.details = details;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = parseStartTime(startTime);
    }

    public Task(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public Task(String name, String details, int id, Status status, String startTime, long duration) {
        this.name = name;
        this.details = details;
        this.id = id;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = parseStartTime(startTime);
    }

    public Task(int id, String name, Status status, String details) {
        this.name = name;
        this.details = details;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String details, int id) {
        this.name = name;
        this.details = details;
        this.id = id;
    }

    public Task(String name, String details, int id, Status status) {
        this.name = name;
        this.details = details;
        this.id = id;
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            LocalDateTime endTime = startTime.plus(duration);
            return endTime;
        } else {
            return null;
        }
    }

    public String getFormattedDuration() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    private LocalDateTime parseStartTime(String startTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        try {
            return LocalDateTime.parse(startTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Некорректный формат времени" + startTimeString, e);
        }

    }

    public String getStartTimeInString() {
        return parseStartTimeBack(startTime);
    }

    private String parseStartTimeBack(LocalDateTime startTime) {
        if (startTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return startTime.format(formatter);
        } else {
            return null;
        }
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(details, task.details) && status == task.status && type == task.type && Objects.equals(startTime, task.startTime) && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, details, id, status, type, startTime, duration);
    }
}
