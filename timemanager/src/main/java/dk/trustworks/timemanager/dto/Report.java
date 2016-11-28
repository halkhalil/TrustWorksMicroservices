package dk.trustworks.timemanager.dto;

public class Report {

    public String clientName;

    public String projectName;

    public String taskUUID;

    public String taskName;

    public String workerUUID;

    public String workerName;

    public double hours;

    public double rate;

    public double sum;

    public Report() {
    }

    public Report(String clientName, String projectName, String taskUUID, String taskName, String workerUUID, String workerName, double hours, double rate, double sum) {
        this.clientName = clientName;
        this.projectName = projectName;
        this.taskUUID = taskUUID;
        this.taskName = taskName;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.hours = hours;
        this.rate = rate;
        this.sum = sum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Report{");
        sb.append("clientName='").append(clientName).append('\'');
        sb.append(", projectName='").append(projectName).append('\'');
        sb.append(", taskUUID='").append(taskUUID).append('\'');
        sb.append(", taskName='").append(taskName).append('\'');
        sb.append(", workerUUID='").append(workerUUID).append('\'');
        sb.append(", workerName='").append(workerName).append('\'');
        sb.append(", hours=").append(hours);
        sb.append(", rate=").append(rate);
        sb.append(", sum=").append(sum);
        sb.append('}');
        return sb.toString();
    }
}
