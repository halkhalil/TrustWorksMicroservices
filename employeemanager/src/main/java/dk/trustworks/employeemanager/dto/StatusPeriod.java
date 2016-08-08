package dk.trustworks.employeemanager.dto;

import org.joda.time.LocalDate;

/**
 * Created by hans on 15/07/16.
 */
public class StatusPeriod {

    private String employeeUUID;
    private LocalDate date;
    private double salary;
    private double hours;
    private String status;

    public StatusPeriod() {
    }

    public StatusPeriod(String employeeUUID, LocalDate date, double salary, double hours, String status) {
        this.employeeUUID = employeeUUID;
        this.date = date;
        this.salary = salary;
        this.hours = hours;
        this.status = status;
    }

    public String getEmployeeUUID() {
        return employeeUUID;
    }

    public void setEmployeeUUID(String employeeUUID) {
        this.employeeUUID = employeeUUID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatusPeriod{");
        sb.append("employeeUUID='").append(employeeUUID).append('\'');
        sb.append(", date=").append(date);
        sb.append(", salary=").append(salary);
        sb.append(", hours=").append(hours);
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
