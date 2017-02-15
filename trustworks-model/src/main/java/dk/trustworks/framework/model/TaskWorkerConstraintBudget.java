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
//@Api(value = "/budget", description = "Operations about user budgets related to tasks")
public class TaskWorkerConstraintBudget {

    //@JsonIgnore
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

    public TaskWorkerConstraintBudget() {
    }

    public TaskWorkerConstraintBudget(String uuid, double budget, Integer month, Integer year, String useruuid, String taskuuid) {
        this.uuid = uuid;
        this.budget = budget;
        this.month = month;
        this.year = year;
        this.created = DateTime.now();
        this.useruuid = useruuid;
        this.taskuuid = taskuuid;
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
