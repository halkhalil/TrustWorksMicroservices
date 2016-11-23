package dk.trustworks.timemanager.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by hans on 30/10/2016.
 */

@JsonTypeName("weekitem")
public class WeekItem {

    public String uuid;
    public String taskuuid;
    public String useruuid;
    public int weeknumber;
    public int year;
    public int sorting;

}
