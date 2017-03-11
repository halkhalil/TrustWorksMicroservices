package dk.trustworks.timemanager.adminportal.domain;

import org.joda.time.LocalDate;

public class Salary {
  public String uuid;
  public String useruuid;
  public int salary;
  public LocalDate activeDate;

  public Salary() {
  }

  public Salary(String uuid, String useruuid, int salary, LocalDate activeDate) {
    this.uuid = uuid;
    this.useruuid = useruuid;
    this.salary = salary;
    this.activeDate = activeDate;
  }
}
