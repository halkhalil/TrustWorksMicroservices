package dk.trustworks.bimanager.dto.numerics;

import org.joda.time.LocalDate;

/**
 * Created by hans on 17/12/2016.
 */
public class Data {

    public LocalDate date;
    public long value;
    public String name;

    public Data() {
    }

    public Data(long value) {
        this.value = value;
    }

    public Data(long value, String name) {
        this.value = value;
        this.name = name;
    }

    public Data(long value, LocalDate date) {
        this.date = date;
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Data{");
        sb.append("date=").append(date);
        sb.append(", value=").append(value);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
