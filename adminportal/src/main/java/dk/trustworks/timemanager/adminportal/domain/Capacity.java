package dk.trustworks.timemanager.adminportal.domain;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Capacity{");
        sb.append("capacity=").append(capacity);
        sb.append(", activeDate=").append(activeDate);
        sb.append('}');
        return sb.toString();
    }
}
