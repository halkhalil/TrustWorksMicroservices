package dk.trustworks.usermanager.dto;

import org.joda.time.LocalDate;

/**
 * Created by hans on 19/10/2016.
 */
public class Availability {

    public String userUUID;
    public LocalDate activeDate;

    public Availability() {
    }

    public Availability(String userUUID, LocalDate activeDate) {
        this.userUUID = userUUID;
        this.activeDate = activeDate;
    }
}
