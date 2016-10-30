package dk.trustworks.usermanager.dto;

/**
 * Created by hans on 24/10/2016.
 */
public class Role {

    public String uuid;
    public String useruuid;
    public String role;

    public Role() {
    }

    public Role(String uuid, String useruuid, String role) {
        this.uuid = uuid;
        this.useruuid = useruuid;
        this.role = role;
    }
}
