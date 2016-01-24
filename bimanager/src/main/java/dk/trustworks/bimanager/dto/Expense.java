package dk.trustworks.bimanager.dto;

import java.util.UUID;

/**
 * Created by hans on 21/01/16.
 */
public class Expense implements Comparable<Expense> {

    private String uuid;
    private int year;
    private int month;
    private ExpenseType type;
    private double expense;
    private ExpenseDescription description;

    public Expense() {
        uuid = UUID.randomUUID().toString();
        expense = 0.0;
        type = ExpenseType.EXPENSE;
        description = ExpenseDescription.Lønninger;
        year = 201;
        month = 0;
    }

    public Expense(String uuid, int year, int month, ExpenseType type, double expense, ExpenseDescription description) {
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

    public ExpenseType getType() {
        return type;
    }

    public void setType(ExpenseType type) {
        this.type = type;
    }

    public ExpenseDescription getDescription() {
        return description;
    }

    public void setDescription(ExpenseDescription description) {
        this.description = description;
    }

    @Override
    public int compareTo(Expense o) {
        return Integer.compare(o.month, this.month);
    }
}
