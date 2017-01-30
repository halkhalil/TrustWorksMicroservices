package dk.trustworks.framework.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {

    public String uuid;

    public String customerreference;

    public String name;

    public double budget;

    public String clientuuid;

    public String clientdatauuid;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    public Date enddate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    public Date startdate;

    @JsonIgnore
    public DateTime created;

    public String userowneruuid;

    public boolean active;

    public List<Task> tasks = new ArrayList<>();

    public Project() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Project{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", customerreference='").append(customerreference).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", budget=").append(budget);
        sb.append(", clientuuid='").append(clientuuid).append('\'');
        sb.append(", clientdatauuid='").append(clientdatauuid).append('\'');
        sb.append(", enddate=").append(enddate);
        sb.append(", startdate=").append(startdate);
        sb.append(", created=").append(created);
        sb.append(", userowneruuid='").append(userowneruuid).append('\'');
        sb.append(", active=").append(active);
        sb.append(", tasks=").append(tasks);
        sb.append('}');
        return sb.toString();
    }
}
