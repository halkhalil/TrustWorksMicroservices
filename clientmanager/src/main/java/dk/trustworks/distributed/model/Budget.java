package dk.trustworks.distributed.model;

/**
 * Created by hans on 16/10/2016.
 */
public class Budget {

    private String userUUID;
    private double budget;

    public Budget() {
    }

    public Budget(String userUUID, double budget) {
        this.userUUID = userUUID;
        this.budget = budget;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
