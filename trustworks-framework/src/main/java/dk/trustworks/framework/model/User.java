package dk.trustworks.framework.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;

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

    public String UUID;

    public String useruuid;

    public String username = "";

    public String password = "";

    public String firstname = "";

    public String lastname = "";

    public String email = "";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date created;

    public boolean active = true;

    public String status;

    public String statusdate;

    public Long allocation;

    public Revenue revenue;

    public User() {
        UUID = java.util.UUID.randomUUID().toString();
        created = new Date();
    }

    public User(String UUID, String username, String password, String firstname, String lastname, String email, Date created, boolean active, String useruuid, String status, String statusdate, Long allocation) {
        this.UUID = UUID;
        this.useruuid = useruuid;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.created = created;
        this.active = active;
        this.status = status;
        this.statusdate = statusdate;
        this.allocation = allocation;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Revenue getRevenue() {
        return revenue;
    }

    public void setRevenue(Revenue revenue) {
        this.revenue = revenue;
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
        sb.append(", revenue=").append(revenue);
        sb.append('}');
        return sb.toString();
    }
}
