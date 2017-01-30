package dk.trustworks.framework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 24/09/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {

    public String uuid;
    public boolean active;
    public String contactname;
    public DateTime created;
    public String name;
    public List<Project> projects = new ArrayList<>();

    @JsonIgnore
    public double latitude;

    @JsonIgnore
    public double longitude;

    public Client() {
    }

    public Client(String uuid, boolean active, String contactname, DateTime created, String name) {
        this.uuid = uuid;
        this.active = active;
        this.contactname = contactname;
        this.created = created;
        this.name = name;
    }
}
