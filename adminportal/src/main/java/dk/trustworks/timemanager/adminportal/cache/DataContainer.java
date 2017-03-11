package dk.trustworks.timemanager.adminportal.cache;

import dk.trustworks.timemanager.adminportal.domain.*;
import dk.trustworks.framework.model.User;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 07/12/2016.
 */
public class DataContainer {

    private final LocalDate periodStart;
    private final LocalDate periodEnd;

    private final DataAccess dataAccess;
    private final List<Salary> userSalaryPerMonthList;
    private final long[] capacityPerMonth;
    private final List<Expense> expensesByMonth;
    private final List<AmountPerItem> userAvailabilityPerMonthList;
    private final List<User> users;
    private final List<AmountPerItem> workRegistrationDelay;
    private final double revenueRate;
    private final List<AmountPerItem> projectRevenue;
    private final long[] revenuePerMonth;
    private final List<AmountPerItem> userRevenue;
    private final List<AmountPerItem> billableHoursPerUser;


    public DataContainer(LocalDate periodStart, LocalDate periodEnd) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        dataAccess = new DataAccess();
        long time = System.currentTimeMillis();
        userSalaryPerMonthList = null;//dataAccess.getUserSalaryPerMonthByYear(periodStart, periodEnd);
        time = System.currentTimeMillis() - time;
        System.out.println("userSalaryPerMonthList = " + time);
        time = System.currentTimeMillis();
        capacityPerMonth = dataAccess.getCapacityPerMonth(periodStart, periodEnd);
        time = System.currentTimeMillis() - time;
        System.out.println("capacityPerMonth = " + time);
        time = System.currentTimeMillis();
        expensesByMonth = dataAccess.getExpensesByPeriod(periodStart, periodEnd);
        time = System.currentTimeMillis() - time;
        System.out.println("expensesByMonth = " + time);
        time = System.currentTimeMillis();
        userAvailabilityPerMonthList = dataAccess.getUserAvailabilityPerMonthByYear(periodStart, periodEnd);
        time = System.currentTimeMillis() - time;
        System.out.println("userAvailabilityPerMonthList = " + time);
        time = System.currentTimeMillis();
        users = dataAccess.getUsers();
        time = System.currentTimeMillis() - time;
        System.out.println("users = " + time);
        time = System.currentTimeMillis();
        workRegistrationDelay = new ArrayList<>(); //dataAccess.getWorkRegistrationDelay(2016);
        time = System.currentTimeMillis() - time;
        System.out.println("workRegistrationDelay = " + time);
        revenueRate = dataAccess.getRevenueRate();
        time = System.currentTimeMillis();
        revenuePerMonth = dataAccess.getRevenuePerMonth(periodStart, periodEnd);
        time = System.currentTimeMillis() - time;
        System.out.println("revenuePerMonth = " + time);
        time = System.currentTimeMillis();
        projectRevenue = dataAccess.getProjectRevenue(periodStart, periodEnd);
        time = System.currentTimeMillis() - time;
        System.out.println("projectRevenue = " + time);
        time = System.currentTimeMillis();
        userRevenue = dataAccess.getUserRevenue(periodStart, periodEnd);
        time = System.currentTimeMillis() - time;
        System.out.println("userRevenue = " + time);
        billableHoursPerUser = dataAccess.getBillableHoursPerUser(periodStart, periodEnd);
        time = System.currentTimeMillis() - time;
        System.out.println("billableHoursPerUser = " + time);
    }

    public List<Salary> getUserSalaryPerMonthList() {
        return userSalaryPerMonthList;
    }

    public long[] getCapacityPerMonth() {
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

    public List<AmountPerItem> getUserAvailabilityPerMonthList() {
        return userAvailabilityPerMonthList;
    }

    public List<AmountPerItem> getUserAvailabilityPerMonthList(LocalDate periodStart, LocalDate periodEnd) {
        return dataAccess.getUserAvailabilityPerMonthByYear(periodStart, periodEnd);
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

    public List<AmountPerItem> getBillableHoursPerUser() {
        return billableHoursPerUser;
    }

    public List<AmountPerItem> getBillableHoursPerUser(LocalDate periodStart, LocalDate periodEnd) {
        return dataAccess.getBillableHoursPerUser(periodStart, periodEnd);
    }

    public List<AmountPerItem> getFreeDaysPerMonthPerUser(LocalDate periodStart, LocalDate periodEnd) {
        return dataAccess.getFreeDaysPerMonthPerUser(periodStart, periodEnd);
    }

    public List<AmountPerItem> getSickDaysPerMonthPerUser(LocalDate periodStart, LocalDate periodEnd) {
        return dataAccess.getSickDaysPerMonthPerUser(periodStart, periodEnd);
    }
}
