package dk.trustworks.framework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.joda.time.DateTime;

/**
 * Created by hans on 12/05/15.
 */
@JsonTypeName("budget")
@JsonIgnoreProperties({"created"})
public class Budget {

    @JsonIgnore
    public String uuid;

    public double budget;

    public Integer month;

    public Integer year;

    @Deprecated
    @JsonIgnore
    public String taskworkerconstraintuuid;

    public DateTime created;

    public String useruuid;

    public String taskuuid;

    public Budget() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskWorkerConstraintBudget{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", budget=").append(budget);
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", taskworkerconstraintuuid='").append(taskworkerconstraintuuid).append('\'');
        sb.append(", created=").append(created);
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", taskuuid='").append(taskuuid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
