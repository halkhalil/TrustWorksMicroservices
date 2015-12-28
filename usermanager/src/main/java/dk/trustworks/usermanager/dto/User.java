package dk.trustworks.usermanager.dto;

import java.util.Date;

/**
 * Created by hans on 16/03/15.
 */
public class User {

    private String UUID;

    private String username = "";

    private String password = "";

    private String firstname = "";

    private String lastname = "";

    private String email = "";

    private Date created;

    private boolean active = true;

    public User() {
        UUID = java.util.UUID.randomUUID().toString();
        created = new Date();
    }

    public User(String username, String password, String firstname, String lastname, String email) {
        this.UUID = java.util.UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.created = new Date();
        this.active = true;
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
}
