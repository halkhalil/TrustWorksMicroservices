package dk.trustworks.clientmanager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * Created by hans on 16/03/15.
 *
 * A = Active
 * D = Deceased
 * L = On Non-Pay Leave
 * N = Status Not Yet Processed
 * P = Processing
 * R = Retired
 * T = Terminated
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private String UUID;

    private String useruuid;

    private String username = "";

    private String password = "";

    private String firstname = "";

    private String lastname = "";

    private String email = "";

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date created;

    private boolean active = true;

    private String status;

    private String statusdate;

    private Long allocation;

    public User() {
        UUID = java.util.UUID.randomUUID().toString();
        created = new Date();
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(String statusdate) {
        this.statusdate = statusdate;
    }

    public Long getAllocation() {
        return allocation;
    }

    public void setAllocation(Long allocation) {
        this.allocation = allocation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("UUID='").append(UUID).append('\'');
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", created=").append(created);
        sb.append(", active=").append(active);
        sb.append(", status='").append(status).append('\'');
        sb.append(", statusdate='").append(statusdate).append('\'');
        sb.append(", allocation=").append(allocation);
        sb.append('}');
        return sb.toString();
    }
}
