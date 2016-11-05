package dk.trustworks.timemanager.dto;

import org.joda.time.DateTime;

import java.time.LocalDate;

/**
 * Created by hans on 30/10/2016.
 */
public class Work {

    public String uuid;
    public int day;
    public int month;
    public int year;
    public String useruuid;
    public String taskuuid;
    public double workduration;
    public DateTime created;

}
