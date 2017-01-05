package dk.trustworks.framework.model;

import org.joda.time.LocalDate;

/**
 * Created by hans on 19/10/2016.
 */
public class Capacity {

    public int capacity;

    public LocalDate activeDate;

    public Capacity() {
    }

    public Capacity(int capacity, LocalDate activeDate) {
        this.capacity = capacity;
        this.activeDate = activeDate;
    }
}
