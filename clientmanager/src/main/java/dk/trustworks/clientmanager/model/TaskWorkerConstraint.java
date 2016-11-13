package dk.trustworks.clientmanager.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("taskuserprice")
public class TaskWorkerConstraint {

    public String uuid;

    public double price;

    public String useruuid;

    public String taskuuid;

    public TaskWorkerConstraint() {
    }

    public TaskWorkerConstraint(String uuid, double price, String useruuid, String taskuuid) {
        this.uuid = uuid;
        this.price = price;
        this.useruuid = useruuid;
        this.taskuuid = taskuuid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskWorkerConstraint{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", price=").append(price);
        sb.append(", useruuid='").append(useruuid).append('\'');
        sb.append(", taskuuid='").append(taskuuid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
