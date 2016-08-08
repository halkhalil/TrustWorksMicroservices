package dk.trustworks.employeemanager.dto;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import org.joda.time.DateTime;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by hans on 14/07/16.
 */
public class Employee implements HalResource {

    private String uuid;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private double hours;
    private double salary;
    private String email;
    private DateTime created;

    public Employee() {
    }

    public Employee(String uuid, String username, String password, String firstname, String lastname, double hours, double salary, String email, DateTime created) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.hours = hours;
        this.salary = salary;
        this.email = email;
        this.created = created;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", hours=").append(hours);
        sb.append(", salary=").append(salary);
        sb.append(", email='").append(email).append('\'');
        sb.append(", created=").append(created);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public HalRepresentation.HalRepresentationBuilder representationBuilder() {
        return HalRepresentation.builder()
                .addProperty("uuid", uuid)
                .addProperty("username", username)
                .addProperty("firstname", firstname)
                .addProperty("lastname", lastname)
                .addProperty("email", email)
                .addProperty("created", created)
                .addProperty("hours", hours)
                .addProperty("salary", salary)
                .addLink("self", this);
    }

    @Override
    public URI location() {
        try {
            return new URI("/employees/" + uuid);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
