package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.Revenue;
import dk.trustworks.clientmanager.model.Task;
import dk.trustworks.clientmanager.model.TaskWorkerConstraint;
import dk.trustworks.clientmanager.model.Work;
import dk.trustworks.clientmanager.numericsmodel.NumericsData;
import dk.trustworks.clientmanager.numericsmodel.NumericsNumber;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import net.sf.cglib.proxy.Enhancer;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class RevenueService {

    private WorkService workService;
    private TaskService taskService;
    private TaskWorkerConstraintService taskWorkerConstraintService;

    public RevenueService() {
    }

    public RevenueService(DataSource ds) {
        this.workService = WorkService.getInstance();
        taskService = TaskService.getInstance(ds);
        taskWorkerConstraintService = TaskWorkerConstraintService.getInstance(ds);
    }

    public static RevenueService getInstance(DataSource ds) {
        RevenueService clientService = new RevenueService(ds);
        return (RevenueService) Enhancer.create(clientService.getClass(), new Authenticator(clientService));
    }

    @RoleRight("tm.admin")
    public Collection<Revenue> revenuePerDay(LocalDate periodStart, LocalDate periodEnd) {
        List<Work> workList = workService.findByPeriod(periodStart, periodEnd);
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll("");

        ArrayList<Revenue> revenues = new ArrayList<>();
        revenues.addAll(getStringRevenueMap(periodStart, periodEnd, workList, taskWorkerConstraints, "yyyyMMdd", Period.days(1)).values());
        Collections.sort(revenues);
        return revenues;
    }

    @RoleRight("tm.admin")
    public Collection<Revenue> revenuePerDayPerProject(LocalDate periodStart, LocalDate periodEnd, String projectUUID) {
        List<Task> tasks = taskService.findByProjectUUID(projectUUID, "");
        List<Work> workList = new ArrayList<>();
        for (Task task : tasks) {
            workList.addAll(workService.findByPeriodAndTaskUUID(periodStart, periodEnd, task.uuid));
        }
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll("");

        ArrayList<Revenue> revenues = new ArrayList<>();
        revenues.addAll(getStringRevenueMap(periodStart, periodEnd, workList, taskWorkerConstraints, "yyyyMMdd", Period.days(1)).values());
        Collections.sort(revenues);
        return revenues;
    }

    @RoleRight("tm.admin")
    public Collection<Revenue> revenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        List<Work> workList = workService.findByPeriod(periodStart, periodEnd);
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll("");

        ArrayList<Revenue> revenues = new ArrayList<>();
        revenues.addAll(getStringRevenueMap(periodStart, periodEnd, workList, taskWorkerConstraints, "yyyyMM", Period.months(1)).values());
        Collections.sort(revenues);
        return revenues;
    }

    @RoleRight("tm.admin")
    public Collection<Revenue> revenuePerMonthPerProject(LocalDate periodStart, LocalDate periodEnd, String projectUUID) {
        List<Task> tasks = taskService.findByProjectUUID(projectUUID, "");
        List<Work> workList = new ArrayList<>();
        for (Task task : tasks) {
            workList.addAll(workService.findByPeriodAndTaskUUID(periodStart, periodEnd, task.uuid));
        }
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll("");

        ArrayList<Revenue> revenues = new ArrayList<>();
        revenues.addAll(getStringRevenueMap(periodStart, periodEnd, workList, taskWorkerConstraints, "yyyyMM", Period.months(1)).values());
        Collections.sort(revenues);
        return revenues;
    }

    @RoleRight("tm.admin")
    public Collection<Revenue> revenuePerYear(LocalDate periodStart, LocalDate periodEnd) {
        List<Work> workList = workService.findByPeriod(periodStart, periodEnd);
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll("");

        ArrayList<Revenue> revenues = new ArrayList<>();
        revenues.addAll(getStringRevenueMap(periodStart, periodEnd, workList, taskWorkerConstraints, "yyyy", Period.years(1)).values());
        Collections.sort(revenues);
        return revenues;
    }

    @RoleRight("tm.admin")
    public Collection<Revenue> revenuePerYearPerProject(LocalDate periodStart, LocalDate periodEnd, String projectUUID) {
        List<Task> tasks = taskService.findByProjectUUID(projectUUID, "");
        List<Work> workList = new ArrayList<>();
        for (Task task : tasks) {
            workList.addAll(workService.findByPeriodAndTaskUUID(periodStart, periodEnd, task.uuid));
        }
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll("");

        ArrayList<Revenue> revenues = new ArrayList<>();
        revenues.addAll(getStringRevenueMap(periodStart, periodEnd, workList, taskWorkerConstraints, "yyyy", Period.years(1)).values());
        Collections.sort(revenues);
        return revenues;
    }

    private Map<String, Revenue> getStringRevenueMap(LocalDate periodStart, LocalDate periodEnd, List<Work> workList, List<TaskWorkerConstraint> taskWorkerConstraints, String datePart, Period period) {
        Map<String, Revenue> dateRevenue = new HashMap<>();

        while(periodStart.isBefore(periodEnd.plus(period))) {
            dateRevenue.put(periodStart.toString(datePart), new Revenue(periodStart, 0.0));
            periodStart = periodStart.plus(period);
        }
        for (Work work : workList) {
            for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraints) {
                if(work.taskuuid.equals(taskWorkerConstraint.taskuuid) && work.useruuid.equals(taskWorkerConstraint.useruuid)) {
                    LocalDate localDate = new LocalDate(work.year, work.month+1, work.day);
                    if(dateRevenue.get(localDate.toString(datePart))==null) {
                        System.out.println("localDate = " + localDate);
                        System.out.println("work = " + work);
                    }
                    dateRevenue.get(localDate.toString(datePart)).revenue += (work.workduration * taskWorkerConstraint.price);
                }
            }
        }
        return dateRevenue;
    }

    public NumericsNumber getYearRevenue(int year) {
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll("");
        NumericsNumber numericsNumber = new NumericsNumber("Year to date revenue");
        NumericsData numericsData = new NumericsData();
        numericsNumber.data = numericsData;

        List<Work> workList = workService.findByPeriod(LocalDate.now().minusYears(1), LocalDate.now());
        System.out.println("workList.size() = " + workList.size());

        for (Work work : workList) {
            for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraints) {
                if(work.taskuuid.equals(taskWorkerConstraint.taskuuid) && work.useruuid.equals(taskWorkerConstraint.useruuid)) {
                    numericsNumber.data.value += (work.workduration * taskWorkerConstraint.price);
                }
            }
        }
        return numericsNumber;
    }

}
