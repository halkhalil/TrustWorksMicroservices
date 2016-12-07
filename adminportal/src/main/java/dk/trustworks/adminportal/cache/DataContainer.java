package dk.trustworks.adminportal.cache;

import dk.trustworks.adminportal.domain.*;
import dk.trustworks.framework.model.User;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.List;

/**
 * Created by hans on 07/12/2016.
 */
public class DataContainer {

    private final LocalDate periodStart;
    private final LocalDate periodEnd;

    private final DataAccess dataAccess;
    private final List<Salary> userSalaryPerMonthList;
    private final List<Capacity> capacityPerMonth;
    private final List<Expense> expensesByMonth;
    private final List<Availability> userAvailabilityPerMonthList;
    private final List<User> users;
    private final List<AmountPerItem> workRegistrationDelay;
    private final double revenueRate;
    private final List<AmountPerItem> projectRevenue;
    private final long[] revenuePerMonth;
    private final List<AmountPerItem> userRevenue;


    public DataContainer(LocalDate periodStart, LocalDate periodEnd) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        dataAccess = new DataAccess();
        userSalaryPerMonthList = dataAccess.getUserSalaryPerMonthByYear(periodStart, periodEnd);
        capacityPerMonth = dataAccess.getCapacityPerMonth(periodStart, periodEnd);
        expensesByMonth = dataAccess.getExpensesByPeriod(periodStart, periodEnd);
        userAvailabilityPerMonthList = dataAccess.getUserAvailabilityPerMonthByYear(periodStart, periodEnd);
        users = dataAccess.getUsers();
        workRegistrationDelay = dataAccess.getWorkRegistrationDelay(2016);
        revenueRate = dataAccess.getRevenueRate();
        revenuePerMonth = dataAccess.getRevenuePerMonth(periodStart, periodEnd);
        projectRevenue = dataAccess.getProjectRevenue(periodStart, periodEnd);
        userRevenue = dataAccess.getUserRevenue(periodStart, periodEnd);
    }

    public List<Salary> getUserSalaryPerMonthList() {
        return userSalaryPerMonthList;
    }

    public List<Capacity> getCapacityPerMonth() {
        return capacityPerMonth;
    }

    public long[] getExpensesByMonth(ExpenseType expenseType) {
        long[] expensesPerMonth = new long[12];

        for (Expense expense : expensesByMonth) {
            if(expenseType != null && expenseType != expense.getType()) continue;
            LocalDate localDate = new LocalDate(expense.getYear(), expense.getMonth()+1, 1);
            expensesPerMonth[new Period(periodStart, localDate, PeriodType.months()).getMonths()] += expense.getExpense();
        }

        return expensesPerMonth;
    }

    public List<Availability> getUserAvailabilityPerMonthList() {
        return userAvailabilityPerMonthList;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<AmountPerItem> getWorkRegistrationDelay() {
        return workRegistrationDelay;
    }

    public long[] getRevenuePerMonthPerUser(String userUUID) {
        return dataAccess.getRevenuePerMonthPerUser(periodStart, periodEnd, userUUID);
    }

    public double getRevenueRate() {
        return revenueRate;
    }

    public List<AmountPerItem> getProjectRevenue() {
        return projectRevenue;
    }

    public long[] getRevenuePerMonth() {
        return revenuePerMonth;
    }

    public long[] getBudgetPerMonth(int ahead){
        return dataAccess.getBudgetPerMonth(periodStart, periodEnd, ahead);
    }

    public List<AmountPerItem> getUserRevenue() {
        return userRevenue;
    }
}
