package dk.trustworks.framework.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import org.joda.time.LocalDate;

/**
 * Created by hans on 19/10/2016.
 */
public class Capacity {

    public int capacity;

    @JsonDeserialize(using=LocalDateDeserializer.class)
    public LocalDate activeDate;

    public Capacity() {
    }

    public Capacity(int capacity, LocalDate activeDate) {
        this.capacity = capacity;
        this.activeDate = activeDate;
    }
}
