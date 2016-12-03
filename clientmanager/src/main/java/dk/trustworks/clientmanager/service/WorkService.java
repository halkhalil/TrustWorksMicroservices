package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.Work;
import dk.trustworks.clientmanager.service.commands.GetWorkByPeriodAndTaskUUIDCommand;
import dk.trustworks.clientmanager.service.commands.GetWorkByProjectCommand;
import dk.trustworks.clientmanager.service.commands.GetWorkByPeriodCommand;
import dk.trustworks.framework.security.JwtModule;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by hans on 19/11/2016.
 */
public class WorkService {

    private static final WorkService instance = new WorkService();

    private WorkService() {
    }

    public static WorkService getInstance() {
        return instance;
    }

    public List<Work> findByProjectUUID(String projectUUID) {
        List<Work> budget = new GetWorkByProjectCommand(projectUUID, JwtModule.JWTTOKEN.get()).execute();
        return budget;
    }

    public List<Work> findByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        List<Work> budget = new GetWorkByPeriodCommand(periodStart, periodEnd, JwtModule.JWTTOKEN.get()).execute();
        return budget;
    }

    public List<Work> findByPeriodAndTaskUUID(LocalDate periodStart, LocalDate periodEnd, String taskUUID) {
        List<Work> budget = new GetWorkByPeriodAndTaskUUIDCommand(periodStart, periodEnd, taskUUID, JwtModule.JWTTOKEN.get()).execute();
        return budget;
    }
}
