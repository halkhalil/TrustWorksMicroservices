package dk.trustworks.usermanager.dto;

public class Userstatus {
    private String uuid;
    private String useruuid;
    private String status;
    private java.sql.Date statusdate;
    private Long allocation;

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

    public java.sql.Date getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(java.sql.Date statusdate) {
        this.statusdate = statusdate;
    }

    public Long getAllocation() {
        return allocation;
    }

    public void setAllocation(Long allocation) {
        this.allocation = allocation;
    }
}
