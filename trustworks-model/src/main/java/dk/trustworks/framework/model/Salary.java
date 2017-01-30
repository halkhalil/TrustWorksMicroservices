package dk.trustworks.framework.model;

import org.joda.time.LocalDate;

public class Salary {
  public String uuid;
  public String useruuid;
  public Long salary;
  public LocalDate activeDate;

  public Salary() {
  }

  public Salary(String uuid, String useruuid, Long salary, LocalDate activeDate) {
    this.uuid = uuid;
    this.useruuid = useruuid;
    this.salary = salary;
    this.activeDate = activeDate;
  }
}
