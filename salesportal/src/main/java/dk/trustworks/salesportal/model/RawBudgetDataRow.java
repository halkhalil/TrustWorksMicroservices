package dk.trustworks.salesportal.model;

import java.util.UUID;

/**
 * Created by hans on 13/03/2017.
 */
public class RawBudgetDataRow {

    public String uuid;
    public String username;
    public String clientname;
    public String projectname;
    public String taskname;
    public int m1;
    public int m2;
    public int m3;
    public int m4;
    public int m5;
    public int m6;
    public int m7;
    public int m8;
    public int m9;
    public int m10;
    public int m11;
    public int m12;

    public RawBudgetDataRow() {
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public int getM1() {
        return m1;
    }

    public void setM1(int m1) {
        this.m1 = m1;
    }

    public int getM2() {
        return m2;
    }

    public void setM2(int m2) {
        this.m2 = m2;
    }

    public int getM3() {
        return m3;
    }

    public void setM3(int m3) {
        this.m3 = m3;
    }

    public int getM4() {
        return m4;
    }

    public void setM4(int m4) {
        this.m4 = m4;
    }

    public int getM5() {
        return m5;
    }

    public void setM5(int m5) {
        this.m5 = m5;
    }

    public int getM6() {
        return m6;
    }

    public void setM6(int m6) {
        this.m6 = m6;
    }

    public int getM7() {
        return m7;
    }

    public void setM7(int m7) {
        this.m7 = m7;
    }

    public int getM8() {
        return m8;
    }

    public void setM8(int m8) {
        this.m8 = m8;
    }

    public int getM9() {
        return m9;
    }

    public void setM9(int m9) {
        this.m9 = m9;
    }

    public int getM10() {
        return m10;
    }

    public void setM10(int m10) {
        this.m10 = m10;
    }

    public int getM11() {
        return m11;
    }

    public void setM11(int m11) {
        this.m11 = m11;
    }

    public int getM12() {
        return m12;
    }

    public void setM12(int m12) {
        this.m12 = m12;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawBudgetDataRow that = (RawBudgetDataRow) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RawBudgetDataRow{");
        sb.append("username='").append(username).append('\'');
        sb.append(", clientname='").append(clientname).append('\'');
        sb.append(", projectname='").append(projectname).append('\'');
        sb.append(", taskname='").append(taskname).append('\'');
        sb.append(", m1=").append(m1);
        sb.append(", m2=").append(m2);
        sb.append(", m3=").append(m3);
        sb.append(", m4=").append(m4);
        sb.append(", m5=").append(m5);
        sb.append(", m6=").append(m6);
        sb.append(", m7=").append(m7);
        sb.append(", m8=").append(m8);
        sb.append(", m9=").append(m9);
        sb.append(", m10=").append(m10);
        sb.append(", m11=").append(m11);
        sb.append(", m12=").append(m12);
        sb.append('}');
        return sb.toString();
    }
}
