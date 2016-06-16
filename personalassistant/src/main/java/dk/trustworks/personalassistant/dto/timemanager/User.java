package dk.trustworks.personalassistant.dto.timemanager;

import java.util.Date;

public class User {

    private String UUID;

    private String username;

    private String password;

    private String firstname;

    private String lastname;

    private String email;

    private Date created;

    private boolean active = true;

    private String useruuid;

    private String status;

    private Date statusdate;

    private int allocation;

    public User() {
    }

    public User(String UUID, String username, String password, String firstname, String lastname, String email, Date created, boolean active, String useruuid, String status, Date statusdate, int allocation) {
        this.UUID = UUID;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.created = created;
        this.active = active;
        this.useruuid = useruuid;
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

    public Date getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(Date statusdate) {
        this.statusdate = statusdate;
    }

    public int getAllocation() {
        return allocation;
    }

    public void setAllocation(int allocation) {
        this.allocation = allocation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("UUID='").append(UUID).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", created=").append(created);
        sb.append(", active=").append(active);
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", statusdate=").append(statusdate);
        sb.append(", allocation=").append(allocation);
        sb.append('}');
        return sb.toString();
    }
}
