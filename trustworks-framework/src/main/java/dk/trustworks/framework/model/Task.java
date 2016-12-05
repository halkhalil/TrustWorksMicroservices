package dk.trustworks.framework.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Task implements Serializable {

    public String uuid;

    public String name;

    public String projectuuid;

    public String type;

    public Project project;

    @JsonProperty("taskuserprice")
    public List<TaskWorkerConstraint> taskworkerconstraints = new ArrayList<>();

    public Task() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", projectuuid='").append(projectuuid).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", project=").append(project);
        sb.append(", taskworkerconstraints=").append(taskworkerconstraints);
        sb.append('}');
        return sb.toString();
    }
}