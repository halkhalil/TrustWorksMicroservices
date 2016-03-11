package dk.trustworks.bimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 24/09/15.
 */
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;
    public String uuid;
    public boolean active;
    public String contactname;
    public String created;
    public String name;

    @JsonProperty("projects")
    public List<Project> projects = new ArrayList<>();

    public Client() {
    }

    public Client(String uuid, boolean active, String contactname, String created, String name) {
        this.uuid = uuid;
        this.active = active;
        this.contactname = contactname;
        this.created = created;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" +
                "uuid='" + uuid + '\'' +
                ", active=" + active +
                ", contactname='" + contactname + '\'' +
                ", created='" + created + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
