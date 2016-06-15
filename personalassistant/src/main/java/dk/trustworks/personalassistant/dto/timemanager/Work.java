package dk.trustworks.personalassistant.dto.timemanager;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Work {

    @JsonProperty("uuid")
    private String UUID;

    private int day;

    private int month;

    private int year;

    @JsonProperty("workduration")
    private double workDuration;

    @JsonProperty("useruuid")
    private String userUUID;

    @JsonProperty("taskuuid")
    private String taskUUID;

    private Date created;

    public Work() {
    }

    public Work(String UUID, int day, int month, int year, double workDuration, String userUUID, String taskUUID, Date created) {
        this.UUID = UUID;
        this.day = day;
        this.month = month;
        this.year = year;
        this.workDuration = workDuration;
        this.userUUID = userUUID;
        this.taskUUID = taskUUID;
        this.created = created;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(double workDuration) {
        this.workDuration = workDuration;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Work{");
        sb.append("UUID='").append(UUID).append('\'');
        sb.append(", day=").append(day);
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", workDuration=").append(workDuration);
        sb.append(", userUUID='").append(userUUID).append('\'');
        sb.append(", taskUUID='").append(taskUUID).append('\'');
        sb.append(", created=").append(created);
        sb.append('}');
        return sb.toString();
    }
}
