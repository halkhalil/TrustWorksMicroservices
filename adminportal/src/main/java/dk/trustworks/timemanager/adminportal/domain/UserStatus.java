package dk.trustworks.timemanager.adminportal.domain;

import java.util.Date;

/**
 * Created by hans on 30/12/15.
 */
public class UserStatus {

    private String uuid;
    private String useruuid;
    private String status;
    private Date statusdate;

    public UserStatus() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
