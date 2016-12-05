package dk.trustworks.timemanager.service;

import dk.trustworks.framework.model.Project;
import dk.trustworks.framework.model.Task;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import dk.trustworks.timemanager.service.commands.GetProjectCommand;
import dk.trustworks.framework.model.Work;
import dk.trustworks.timemanager.persistence.WorkRepository;
import net.sf.cglib.proxy.Enhancer;
import org.joda.time.LocalDate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 17/03/15.
 */
public class WorkService {

    private WorkRepository workRepository;

    public WorkService(DataSource ds) {
        workRepository = new WorkRepository(ds);
    }

    public WorkService() {
    }

    public static WorkService getInstance(DataSource ds) {
        WorkService service = new WorkService(ds);
        return (WorkService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.user")
    public List<Work> findByTaskUUID(String taskUUID) {
        return workRepository.findByTaskUUID(taskUUID);
    }

    @RoleRight("tm.user")
    public List<Work> findByProjectUUID(String projectUUID) {
        System.out.println("WorkService.findByProjectUUID");
        System.out.println("projectUUID = [" + projectUUID + "]");
        Project project = new GetProjectCommand(projectUUID, "task").execute();
        List<Work> workList = new ArrayList<>();
        for (Task task : project.tasks) {
            workList.addAll(workRepository.findByTaskUUID(task.uuid));
        }
        System.out.println("workList.size() = " + workList.size());
        return workList;
    }

    @RoleRight("tm.user")
    public List<Work> findByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return workRepository.findByPeriod(periodStart, periodEnd);
    }

    @RoleRight("tm.user")
    public List<Work> findByPeriodAndTaskUUID(LocalDate periodStart, LocalDate periodEnd, String taskUUID) {
        return workRepository.findByPeriodAndTaskUUID(periodStart, periodEnd, taskUUID);
    }

    @RoleRight("tm.user")
    public List<Work> findByPeriodAndUserUUID(LocalDate periodStart, LocalDate periodEnd, String userUUID) {
        return workRepository.findByPeriodAndUserUUID(periodStart, periodEnd, userUUID);
    }

    @RoleRight("tm.user")
    public List<Work> findByYear(int year) {
        return workRepository.findByYear(year);
    }

    @RoleRight("tm.user")
    public List<Work> findByYearAndUserUUID(String userUUID, int year) {
        return workRepository.findByYearAndUserUUID(year, userUUID);
    }

    @RoleRight("tm.user")
    public List<Work> findByYearAndMonth(int year, int month) {
        return workRepository.findByYearAndMonth(year, month);
    }

    @RoleRight("tm.user")
    public List<Work> findByYearAndMonthAndTaskUUIDAndUserUUID(int year, int month, String taskUUID, String userUUID) {
        return workRepository.findByYearAndMonthAndTaskUUIDAndUserUUID(year, month, taskUUID, userUUID);
    }

    @RoleRight("tm.user")
    public List<Work> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(int year, int month, int day, String taskUUID, String userUUID) {
        return workRepository.findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(year, month, day, taskUUID, userUUID);
    }

    @RoleRight("tm.user")
    public List<Work> findByYearAndMonthAndDay(int year, int month, int day) {
        return workRepository.findByYearAndMonthAndDay(year, month, day);
    }

    @RoleRight("tm.user")
    public List<Work> findByYearAndMonthAndTaskUUID(int year, int month, String taskUUID) {
        return workRepository.findByYearAndMonthAndTaskUUID(year, month, taskUUID);
    }

    @RoleRight("tm.user")
    public List<Work> findByYearAndTaskUUIDAndUserUUID(int year, String taskUUID, String userUUID) {
        return workRepository.findByYearAndTaskUUIDAndUserUUID(year, taskUUID, userUUID);
    }

    @RoleRight("tm.user")
    public double calculateTaskUserTotalDuration(String taskUUID, String userUUID) {
        return workRepository.calculateTaskUserTotalDuration(taskUUID, userUUID);
    }

    @RoleRight("tm.user")
    public void create(Work work) throws SQLException {
        workRepository.create(work);
    }

    @RoleRight("tm.user")
    public void update(Work work, String uuid) throws SQLException {
        workRepository.update(work, uuid);
    }
}
