package dk.trustworks.usermanager.dto;

public class Salary {
  private String uuid;
  private String useruuid;
  private Long salary;
  private java.sql.Date activefrom;

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

  public Long getSalary() {
    return salary;
  }

  public void setSalary(Long salary) {
    this.salary = salary;
  }

  public java.sql.Date getActivefrom() {
    return activefrom;
  }

  public void setActivefrom(java.sql.Date activefrom) {
    this.activefrom = activefrom;
  }
}
