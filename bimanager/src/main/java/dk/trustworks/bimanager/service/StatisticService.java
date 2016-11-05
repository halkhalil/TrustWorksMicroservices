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
        Interval fiscalPeriod = getFiscalPeriod(year, fiscal);

        List<Work> allWork = new ArrayList<>();
        allWork.addAll(restDelegate.getAllWork(year));
        if(fiscal) allWork.addAll(restDelegate.getAllWork(year-1));

        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = restDelegate.getTaskWorkerConstraintMap(restDelegate.getAllProjects());
        Map<String, User> users = restDelegate.getAllUsersMap();

        List<AmountPerItem> listOfUsers = new ArrayList<>();
        Map<String, Double> userWorkHours = new HashMap<>();

        for (Work work : allWork) {
            DateTime workDate = new DateTime(work.getYear(), work.getMonth() + 1, work.getDay(), 10, 10);
            if(!fiscalPeriod.contains(workDate)) continue;
            /*
            if(fiscal) {
                if((work.getYear() == year && work.getMonth() > 5) || (work.getYear() == year-1 && work.getMonth() < 6)) {
                    continue;
                }
            }
            */
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID()+work.getTaskUUID());
            if(taskWorkerConstraint==null || taskWorkerConstraint.getPrice() <= 0.0) continue;
            if(!userWorkHours.containsKey(work.getUserUUID())) userWorkHours.put(work.getUserUUID(), 0.0);
            userWorkHours.put(work.getUserUUID(), userWorkHours.get(work.getUserUUID())+work.getWorkDuration());
        }

        double daysInYear = Days.daysIn(fiscalPeriod).getDays();
        System.out.println("daysInYear = " + daysInYear);
        double workDays = 224.0;
        double workDaysInYearToDate = (workDays / 365) * daysInYear;//dayOfYear;
        System.out.println("workDaysInYearToDate = " + workDaysInYearToDate);
        for (String userUUID : userWorkHours.keySet()) {
            System.out.println("user = " + users.get(userUUID).getUsername());
            double avgCapacityPerUser;

            List<Capacity> capacityPerMonthByYearByUser = restDelegate.getCapacityPerMonthByYearByUser(fiscalPeriod.getStart().toLocalDate(), fiscalPeriod.getEnd().toLocalDate(), userUUID);//gatherFiscalPeriodData(restDelegate.getCapacityPerMonthByYearByUser(year - 1, userUUID), restDelegate.getCapacityPerMonthByYearByUser(year, userUUID), fiscal);

            System.out.print("capacityPerMonthByYearByUser: ");
            for (Capacity i : capacityPerMonthByYearByUser) {
                System.out.print(i + ", ");
            }
            System.out.println();

            //if(year==dt.getYear()) {
            System.out.println("Months.monthsIn(fiscalPeriod).getMonths() = " + Months.monthsIn(fiscalPeriod.withEnd(fiscalPeriod.getEnd().withDayOfMonth(fiscalPeriod.getEnd().dayOfMonth().getMaximumValue()))).getMonths());
                avgCapacityPerUser = capacityPerMonthByYearByUser.stream().mapToInt((x) -> x.capacity).summaryStatistics().getAverage();
                //avgCapacityPerUser = stats.getAverage(); //ArrayUtils.average(capacityPerMonthByYearByUser, Months.monthsIn(fiscalPeriod.withEnd(fiscalPeriod.getEnd().withDayOfMonth(fiscalPeriod.getEnd().dayOfMonth().getMaximumValue()))).getMonths());
            /*} else {
                avgCapacityPerUser = ArrayUtils.average(capacityPerMonthByYearByUser, Months.monthsIn(fiscalPeriod).getMonths());
            }*/
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
        Interval fiscalPeriod;
        if(fiscal) {
            DateTime startDate = new DateTime(year - 1, 6, 30, 0, 0);
            System.out.println("startDate = " + startDate);
            DateTime endDate = new DateTime(year, 6, 30, 0, 0);
            System.out.println("endDate = " + endDate);
            fiscalPeriod = new Interval(startDate.withTimeAtStartOfDay(), endDate.withTimeAtStartOfDay());
            fiscalPeriod = (fiscalPeriod.containsNow())?fiscalPeriod.withEnd(DateTime.now().withTimeAtStartOfDay()):fiscalPeriod;
        } else {
            DateTime startDate = new DateTime(year - 1, 12, 31, 0, 0);
            System.out.println("startDate = " + startDate);
            DateTime endDate = new DateTime(year, 12, 31, 0, 0);
            System.out.println("endDate = " + endDate);
            fiscalPeriod = new Interval(startDate.withTimeAtStartOfDay(), endDate.withTimeAtStartOfDay());
            fiscalPeriod = (fiscalPeriod.containsNow())?fiscalPeriod.withEnd(DateTime.now().withTimeAtStartOfDay()):fiscalPeriod;
        }
        System.out.println("fiscalPeriod = " + fiscalPeriod);
        return fiscalPeriod;
    }

    private int[] gatherFiscalPeriodData(int[] startPeriod, int[] endPeriod, boolean fiscal) {
        if(fiscal) {
            for (int i = 0; i < 6; i++) {
                endPeriod[i + 6] = endPeriod[i];
            }
            for (int i = 0; i < 6; i++) {
                endPeriod[i] = startPeriod[i + 6];
            }
            return endPeriod;
        }
        return endPeriod;
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

