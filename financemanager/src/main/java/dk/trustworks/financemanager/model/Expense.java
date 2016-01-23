package dk.trustworks.financemanager.model;

/**
 * Created by hans on 21/01/16.
 */
public class Expense {

    private String uuid;
    private int year;
    private int month;
    private String type;
    private double expense;
    private String description;

    public Expense() {
    }

    public Expense(String uuid, int year, int month, String type, double expense, String description) {
        this.uuid = uuid;
        this.year = year;
        this.month = month;
        this.type = type;
        this.expense = expense;
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Expense{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", year=").append(year);
        sb.append(", month=").append(month);
        sb.append(", expense=").append(expense);
        sb.append('}');
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
