package dk.trustworks.timemanager.service;

import dk.trustworks.framework.model.*;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import dk.trustworks.timemanager.client.RestClient;
import net.sf.cglib.proxy.Enhancer;
import org.joda.time.LocalDate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 17/03/15.
 */
public class WeekService {

    private WeekItemService weekItemService;
    private WorkService workService;
    private RestClient restClient;

    public WeekService() {
    }

    public WeekService(DataSource ds) {
        weekItemService = new WeekItemService(ds);
        workService = new WorkService(ds);
        restClient = new RestClient();
    }

    public static WeekService getInstance(DataSource ds) {
        WeekService service = new WeekService(ds);
        return (WeekService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.user")
    public List<WeekRow> findByWeekNumberAndYearAndUserUUID(int weekNumber, int year, String userUUID) {
        List<WeekItem> weekItems = weekItemService.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber, year, userUUID);

        List<WeekRow> weekRows = new ArrayList<>(weekItems.size());
        for (WeekItem weekItem : weekItems) {
            WeekRow weekRow = new WeekRow();
            weekRow.taskuuid = weekItem.taskuuid;
            Task task = restClient.getTask(weekItem.taskuuid);
            Project project = restClient.getProject(task.projectuuid);
            Client client = restClient.getClient(project.clientuuid);
            weekRow.taskname = task.name + " / " + project.name + " / " + client.name;

            LocalDate date = LocalDate.now().withYear(year).withWeekOfWeekyear(weekNumber).withDayOfWeek(1);

            for (Budget budget : restClient.getBudget(weekItem.taskuuid, weekItem.useruuid)) {
                weekRow.budgetleft += budget.budget;
            }

            for (int i = 1; i < 8; i++) {
                List<Work> workList = workService.findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth(), weekItem.taskuuid, weekItem.useruuid);

                System.out.println("weekRow.taskname = " + weekRow.taskname);
                System.out.println("weekRow.budgetleft = " + weekRow.budgetleft);

                for (Work work : workList) {
                    System.out.println("work = " + work);
                    weekRow.hours[date.getDayOfWeek() - 1] += work.workduration;
                }
                date = date.plusDays(1);
            }
            double totalDuration = workService.calculateTaskUserTotalDuration(weekItem.taskuuid, weekItem.useruuid);
            double price = restClient.getTaskUserPrice(weekItem.taskuuid, weekItem.useruuid).price;
            weekRow.budgetleft -= (totalDuration * price);
            weekRows.add(weekRow);
        }

        return weekRows;
    }
}
