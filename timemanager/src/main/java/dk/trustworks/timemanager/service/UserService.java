package dk.trustworks.timemanager.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 24/04/15.
 */
public class UserService {//extends DefaultRemoteService {

    //private final RemoteEntity remoteEntity = new RemoteEntity();

    public List<Map<String, Object>> findByParentUUID(String entityName, String parentUUIDName, String parentUUID) {
        throw new RuntimeException("Not implemented");
    }

    public List<Map<String, Object>> getAllEntities(String entityName) {
        throw new RuntimeException("Not implemented");
    }

    public Map<String, Object> getOneEntity(String entityName, String uuid) {
        return null; // remoteEntity.getOneUserEntity(entityName, uuid);
    }

    public void create(com.fasterxml.jackson.databind.JsonNode clientJsonNode) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    public void update(com.fasterxml.jackson.databind.JsonNode clientJsonNode, String uuid) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    public String getResourcePath() {
        return "users";
    }
}
