package dk.trustworks.usermanager.service;

import dk.trustworks.framework.model.Work;
import dk.trustworks.framework.security.JwtModule;
import dk.trustworks.usermanager.service.commands.GetWorkByPeriodAndUserUUIDCommand;
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

    public List<Work> findByPeriodAndUserUUID(LocalDate periodStart, LocalDate periodEnd, String userUUID) {
        List<Work> budget = new GetWorkByPeriodAndUserUUIDCommand(periodStart, periodEnd, userUUID, JwtModule.JWTTOKEN.get()).execute();
        return budget;
    }
}
