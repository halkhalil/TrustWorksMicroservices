package dk.trustworks.salesportal.model;

/**
 * Created by hans on 19/12/2016.
 */
public class UserBudget {

    public String uuid;
    public String name;
    public String date;
    public double budget;

    public UserBudget() {
    }

    public UserBudget(String uuid, String name, String date, double budget) {
        this.uuid = uuid;
        this.name = name;
        this.date = date;
        this.budget = budget;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserBudget{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", date=").append(date);
        sb.append(", budget=").append(budget);
        sb.append('}');
        return sb.toString();
    }
}
