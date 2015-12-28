package dk.trustworks.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintService extends DefaultLocalService {

    private TaskWorkerConstraintRepository taskWorkerConstraintRepository;

    public TaskWorkerConstraintService() {
        taskWorkerConstraintRepository = new TaskWorkerConstraintRepository();
    }

    public List<Map<String, Object>> findByTaskUUID(Map<String, Deque<String>> queryParameters) {
        String taskuuid = queryParameters.get("taskuuid").getFirst();
        return taskWorkerConstraintRepository.findByTaskUUID(taskuuid);
    }

    public Map<String, Object> findByTaskUUIDAndUserUUID(Map<String, Deque<String>> queryParameters) {
        String taskuuid = queryParameters.get("taskuuid").getFirst();
        String useruuid = queryParameters.get("useruuid").getFirst();
        return taskWorkerConstraintRepository.findByTaskUUIDAndUserUUID(taskuuid, useruuid);
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        taskWorkerConstraintRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        taskWorkerConstraintRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return taskWorkerConstraintRepository;
    }

    @Override
    public String getResourcePath() {
        return "taskworkerconstraint";
    }
}
