package dk.trustworks.employeemanager.dto;


import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import org.joda.time.LocalDate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Created by hans on 14/07/16.
 */
public class Contract implements HalResource {

    private String uuid;
    private String empuuid;
    private double hours;
    private double salary;
    private String status;
    private Date validdate;

    public Contract() {
    }

    public Contract(String uuid, String empuuid, double hours, double salary, String status, Date validdate) {
        this.uuid = uuid;
        this.empuuid = empuuid;
        this.hours = hours;
        this.salary = salary;
        this.status = status;
        this.validdate = validdate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmpuuid() {
        return empuuid;
    }

    public void setEmpuuid(String empuuid) {
        this.empuuid = empuuid;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getValiddate() {
        return validdate;
    }

    public void setValiddate(Date validdate) {
        this.validdate = validdate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Contract{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", empuuid='").append(empuuid).append('\'');
        sb.append(", hours=").append(hours);
        sb.append(", salary=").append(salary);
        sb.append(", status='").append(status).append('\'');
        sb.append(", validdate=").append(validdate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public HalRepresentation.HalRepresentationBuilder representationBuilder() {
        return HalRepresentation.builder()
                .addProperty("uuid", uuid)
                .addProperty("hours", hours)
                .addProperty("salary", salary)
                .addProperty("status", status)
                .addProperty("validate", validdate)
                .addLink("employees", URI.create("/employees/"+empuuid))
                .addLink("self", this);
    }

    @Override
    public URI location() {
        try {
            return new URI("/contracts/" + uuid);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
