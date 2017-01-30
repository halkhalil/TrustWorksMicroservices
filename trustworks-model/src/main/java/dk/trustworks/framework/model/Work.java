package dk.trustworks.framework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;

/**
 * Created by hans on 30/10/2016.
 */
public class Work {

    public String uuid;
    public int day;
    public int month;
    public int year;
    public String useruuid;
    public String taskuuid;
    public double workduration;
    @JsonIgnore public DateTime created;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Work{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", day=").append(day);
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", taskuuid='").append(taskuuid).append('\'');
        sb.append(", workduration=").append(workduration);
        sb.append(", created=").append(created);
        sb.append('}');
        return sb.toString();
    }
}
