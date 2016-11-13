package dk.trustworks.clientmanager.model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 24/09/15.
 */
public class Client {

    public String uuid;
    public boolean active;
    public String contactname;
    public DateTime created;
    public String name;
    public List<Project> projects = new ArrayList<>();

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
