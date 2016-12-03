package dk.trustworks.clientmanager.model;

import org.joda.time.LocalDate;

/**
 * Created by hans on 02/12/2016.
 */
public class Revenue implements Comparable<Revenue> {

    public LocalDate date;
    public double revenue;

    public Revenue() {
    }

    public Revenue(LocalDate date, double revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Revenue{");
        sb.append("date=").append(date);
        sb.append(", revenue=").append(revenue);
        sb.append('}');
        return sb.toString();
    }

    public int compareTo(Revenue o)
    {
        return(date.compareTo(o.date));
    }
}
