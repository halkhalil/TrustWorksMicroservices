package dk.trustworks.timemanager.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("uuid")
    public String UUID;

    public String useruuid;

    public String username;

    public String password;

    public String firstname;

    public String lastname;

    public String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date created;

    public boolean active;

    public String status;

    public String statusdate;

    public Long allocation;

    public User() {
    }

    public User(String UUID, String useruuid, String username, String password, String firstname, String lastname, String email, Date created, boolean active, String status, String statusdate, Long allocation) {
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
