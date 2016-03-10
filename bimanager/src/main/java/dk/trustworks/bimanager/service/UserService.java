package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.framework.network.Locator;
import dk.trustworks.framework.network.RemoteEntity;
import dk.trustworks.framework.service.DefaultRemoteService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 24/04/15.
 */
public class UserService<T> extends DefaultRemoteService {

    private final RemoteEntity remoteEntity = new RemoteEntity();

    @Override
    public List<Map<String, Object>> findByParentUUID(String entityName, String parentUUIDName, String parentUUID) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Map<String, Object>> getAllEntities(String entityName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, Object> getOneEntity(String entityName, String uuid) {
        return remoteEntity.getOneUserEntity(entityName, uuid);
    }

    @Override
    public void create(com.fasterxml.jackson.databind.JsonNode clientJsonNode) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update(com.fasterxml.jackson.databind.JsonNode clientJsonNode, String uuid) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getResourcePath() {
        return "users";
    }
}
