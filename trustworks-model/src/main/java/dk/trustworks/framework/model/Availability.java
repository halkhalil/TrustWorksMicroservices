package dk.trustworks.framework.model;

import org.joda.time.LocalDate;

/**
 * Created by hans on 19/10/2016.
 */
public class Availability {

    public String useruuid;
    public LocalDate activeDate;

    public Availability() {
    }

    public Availability(String useruuid, LocalDate activeDate) {
        this.useruuid = useruuid;
        this.activeDate = activeDate;
    }
}
