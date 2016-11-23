package dk.trustworks.clientmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectBudget {

    public double budget;

    @JsonProperty("remainingbudget")
    public double remainingBudget;

    @JsonProperty("usedbudget")
    public double usedBudget;

    @JsonProperty("assignedbudget")
    public double assignedBudget;

    @JsonProperty("projectuuid")
    public String projectUUID;

    public ProjectBudget() {
    }

    public ProjectBudget(double budget, double remainingBudget, double usedBudget, double assignedBudget, String projectUUID) {
        this.budget = budget;
        this.remainingBudget = remainingBudget;
        this.usedBudget = usedBudget;
        this.assignedBudget = assignedBudget;
        this.projectUUID = projectUUID;
    }
}
