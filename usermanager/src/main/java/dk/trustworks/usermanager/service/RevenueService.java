package dk.trustworks.usermanager.service;

import dk.trustworks.framework.model.Revenue;
import dk.trustworks.framework.model.TaskWorkerConstraint;
import dk.trustworks.framework.model.User;
import dk.trustworks.framework.model.Work;
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
    private UserService userService;
    private TaskWorkerConstraintService taskWorkerConstraintService;

    public RevenueService() {
    }

    public RevenueService(DataSource ds) {
        this.workService = WorkService.getInstance();
        userService = UserService.getInstance(ds);
        taskWorkerConstraintService = TaskWorkerConstraintService.getInstance();
    }

    public static RevenueService getInstance(DataSource ds) {
        RevenueService clientService = new RevenueService(ds);
        return (RevenueService) Enhancer.create(clientService.getClass(), new Authenticator(clientService));
    }

    @RoleRight("tm.admin")
    public Revenue revenuePerUser(LocalDate periodStart, LocalDate periodEnd, String userUUID) {
        List<Work> workList = workService.findByPeriodAndUserUUID(periodStart, periodEnd, userUUID);
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll();


        Revenue revenue = new Revenue(periodEnd, 0.0, userUUID, "");
        for (Work work : workList) {
            for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraints) {
                if(work.taskuuid.equals(taskWorkerConstraint.taskuuid) && work.useruuid.equals(taskWorkerConstraint.useruuid)) {
                    revenue.revenue += (work.workduration * taskWorkerConstraint.price);
                }
            }
        }
        return revenue;
    }

    @RoleRight("tm.admin")
    public Collection<Revenue> revenuePerMonthPerUser(LocalDate periodStart, LocalDate periodEnd, String userUUID) {
        List<Work> workList = new ArrayList<>();
        workList.addAll(workService.findByPeriodAndUserUUID(periodStart, periodEnd, userUUID));
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintService.findAll();

        ArrayList<Revenue> revenues = new ArrayList<>();
        revenues.addAll(getStringRevenueMap(periodStart, periodEnd, workList, taskWorkerConstraints, "yyyyMM", Period.months(1)).values());
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

}
