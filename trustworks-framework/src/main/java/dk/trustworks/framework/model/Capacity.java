package dk.trustworks.framework.model;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;

/**
 * Created by hans on 19/10/2016.
 */
public class Capacity {

    public int capacity;

    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonProperty("activedate")
    public LocalDate activeDate;

    public Capacity() {
    }

    public Capacity(int capacity, LocalDate activeDate) {
        this.capacity = capacity;
        this.activeDate = activeDate;
    }
}
