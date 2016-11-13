package dk.trustworks.timemanager.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by hans on 30/10/2016.
 */

@JsonTypeName("weektasks")
public class Week {

    public String UUID;
    public String taskuuid;
    public String useruuid;
    public int weeknumber;
    public int year;
    public int sorting;

}
