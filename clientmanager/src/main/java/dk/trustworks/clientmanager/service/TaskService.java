package dk.trustworks.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.clientmanager.persistence.TaskRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class TaskService extends DefaultLocalService {

    private TaskRepository taskRepository;

    public TaskService() {
        taskRepository = new TaskRepository();
    }

    public List<Map<String, Object>> findByProjectUUID(Map<String, Deque<String>> queryParameters) {
        String projectUUID = queryParameters.get("projectuuid").getFirst();
        return taskRepository.findByProjectUUID(projectUUID);
    }

    public List<Map<String, Object>> findByProjectUUIDOrderByNameAsc(Map<String, Deque<String>> queryParameters) {
        String projectUUID = queryParameters.get("projectuuid").getFirst();
        return taskRepository.findByProjectUUIDOrderByNameAsc(projectUUID);
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        taskRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        taskRepository.update(jsonNode, uuid);
    }

    @Override
    public String getResourcePath() {
        return "task";
    }

    @Override
    public GenericRepository getGenericRepository() {
        return taskRepository;
    }
}
