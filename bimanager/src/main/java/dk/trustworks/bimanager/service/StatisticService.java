package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.bimanager.client.RestDelegate;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.bimanager.utils.ArrayUtils;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import org.joda.time.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 19/05/15.
 */
public class StatisticService extends DefaultLocalService {

    private RestDelegate restDelegate = RestDelegate.getInstance();

    public StatisticService() {

    }

    @Override
    public GenericRepository getGenericRepository() {
        return null; //taskBudgetRepository;
    }

    @Override
    public String getResourcePath() {
        return "statistics";
    }

    public List<AmountPerItem> billablehourspercentageperuser(int year, boolean fiscal) {
        List<Work> allWork = new ArrayList<>();
        allWork.addAll(restDelegate.getAllWork(year));
        if(fiscal) allWork.addAll(restDelegate.getAllWork(year-1));

        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = restDelegate.getTaskWorkerConstraintMap(restDelegate.getAllProjects());
        Map<String, User> users = restDelegate.getAllUsersMap();

        List<AmountPerItem> listOfUsers = new ArrayList<>();
        Map<String, Double> userWorkHours = new HashMap<>();

        for (Work work : allWork) {
            if(fiscal) {
                if((work.getYear() == year && work.getMonth() > 5) || (work.getYear() == year-1 && work.getMonth() < 6)) {
                    continue;
                }
            }

            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID()+work.getTaskUUID());
            if(taskWorkerConstraint==null || taskWorkerConstraint.getPrice() <= 0.0) continue;
            if(!userWorkHours.containsKey(work.getUserUUID())) userWorkHours.put(work.getUserUUID(), 0.0);
            userWorkHours.put(work.getUserUUID(), userWorkHours.get(work.getUserUUID())+work.getWorkDuration());
        }

        DateTime dt = new DateTime();
        /*
        if(!fiscal && year != new DateTime().getYear()) dt = new DateTime(year, 12, 31, 23, 59);
        if(fiscal && year != new DateTime().getYear()) dt = new DateTime(year, 6, 30, 23, 59);
        double dayOfYear = dt.getDayOfYear();
        LocalDate ld;
        ld = (fiscal)?new LocalDate(year-1,6,1, GJChronology.getInstance()):new LocalDate(year,1,1, GJChronology.getInstance());
        */
        Interval fiscalPeriod = getFiscalPeriod(year, fiscal);
        //double daysInYear = Days.daysBetween(ld,ld.plusYears(1)).getDays();
        double daysInYear = Days.daysIn(fiscalPeriod).getDays();// fiscalPeriod.getDays();//Days.daysBetween(ld,ld.plusYears(1)).getDays();
        System.out.println("daysInYear = " + daysInYear);
        double workDays = 224.0;
        double workDaysInYearToDate = (workDays / daysInYear) * daysInYear;//dayOfYear;
        System.out.println("workDaysInYearToDate = " + workDaysInYearToDate);
        for (String userUUID : userWorkHours.keySet()) {
            System.out.println("user = " + users.get(userUUID).getUsername());
            double avgCapacityPerUser;

            if(year==dt.getYear()) {
                int[] capacityPerMonthByYearByUser = restDelegate.getCapacityPerMonthByYearByUser(year, userUUID);
                if(fiscal) {
                    int[] capacityPerMonthByYearByUserPrevYear = restDelegate.getCapacityPerMonthByYearByUser(year - 1, userUUID);
                    for (int i = 0; i < 6; i++) {
                        capacityPerMonthByYearByUser[i + 6] = capacityPerMonthByYearByUser[i];
                    }
                    for (int i = 0; i < 6; i++) {
                        capacityPerMonthByYearByUser[i] = capacityPerMonthByYearByUserPrevYear[i + 6];
                    }
                }

                System.out.print("capacityPerMonthByYearByUser: ");
                for (int i : capacityPerMonthByYearByUser) {
                    System.out.print(i + ", ");
                }
                System.out.println();
                avgCapacityPerUser = ArrayUtils.average(capacityPerMonthByYearByUser, (dt.monthOfYear().get()>6)?dt.monthOfYear().get()-6:dt.monthOfYear().get()+6);
            } else {
                int[] capacityPerMonthByYearByUser = restDelegate.getCapacityPerMonthByYearByUser(year, userUUID);
                if(fiscal) {
                    int[] capacityPerMonthByYearByUserPrevYear = restDelegate.getCapacityPerMonthByYearByUser(year - 1, userUUID);
                    for (int i = 0; i < 6; i++) {
                        capacityPerMonthByYearByUser[i + 6] = capacityPerMonthByYearByUser[i];
                    }
                    for (int i = 0; i < 6; i++) {
                        capacityPerMonthByYearByUser[i] = capacityPerMonthByYearByUserPrevYear[i + 6];
                    }
                }
                System.out.print("capacityPerMonthByYearByUser: ");
                for (int i : capacityPerMonthByYearByUser) {
                    System.out.print(i + ", ");
                }
                System.out.println();
                avgCapacityPerUser = ArrayUtils.average(capacityPerMonthByYearByUser, 12);
            }
            System.out.println("avgCapacityPerUser = " + avgCapacityPerUser);
            double avgCapacityPerUserPerDay = avgCapacityPerUser / 5.0;
            System.out.println("avgCapacityPerUserPerDay = " + avgCapacityPerUserPerDay);
            double workableHoursInYearToDate = workDaysInYearToDate * avgCapacityPerUserPerDay;

            System.out.println("workableHoursInYearToDate = " + workableHoursInYearToDate);
            System.out.println("userWorkHours = " + userWorkHours.get(userUUID));
            double billableHoursPercentage = (userWorkHours.get(userUUID) / workableHoursInYearToDate) * 100.0;
            System.out.println("billableHoursPercentage = " + billableHoursPercentage);
            listOfUsers.add(new AmountPerItem(userUUID, users.get(userUUID).getFirstname() + " " + users.get(userUUID).getLastname(), billableHoursPercentage));
        }
        System.out.println("\n --- \n");
        return listOfUsers;
    }

    private Interval getFiscalPeriod(int year, boolean fiscal) {
        LocalDate today = LocalDate.now();
        Interval fiscalPeriod;
        if(year == today.getYear()) {
            DateTime startDate = new DateTime(year-1, 12, 31, 0, 0);
            DateTime endDate = DateTime.now();
            fiscalPeriod = (fiscal)?new Interval(startDate.minusMonths(6).withTimeAtStartOfDay(), endDate.minusMonths(6).withTimeAtStartOfDay()) :
                    new Interval(startDate.minusMonths(6).withTimeAtStartOfDay(), endDate.minusMonths(6).withTimeAtStartOfDay());
        } else {
            DateTime startDate = new DateTime(year-1, 12, 31, 0, 0);
            DateTime endDate = new DateTime(year, 12, 31, 0, 0);
            fiscalPeriod = (fiscal)?new Interval(startDate.minusMonths(6).withTimeAtStartOfDay(), endDate.minusMonths(6).withTimeAtStartOfDay()) :
                    new Interval(startDate.minusMonths(6).withTimeAtStartOfDay(), endDate.minusMonths(6).withTimeAtStartOfDay());
        }
        System.out.println("fiscalPeriod = " + fiscalPeriod);
        return fiscalPeriod;
    }

    @Override
    public void create(JsonNode clientJsonNode) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update(JsonNode clientJsonNode, String uuid) throws SQLException {
        throw new RuntimeException("Not implemented");
    }
}

