package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import io.undertow.server.HttpServerExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 19/05/15.
 */
public class StatisticService extends DefaultLocalService {

    private static final Logger log = LogManager.getLogger(StatisticService.class);

    private final RestClient restClient;

    public StatisticService() {
        restClient = new RestClient();
    }

    @Override
    public GenericRepository getGenericRepository() {
        return null; //taskBudgetRepository;
    }

    @Override
    public String getResourcePath() {
        return "statistics";
    }

    @Override
    public void create(JsonNode clientJsonNode) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update(JsonNode clientJsonNode, String uuid) throws SQLException {
        throw new RuntimeException("Not implemented");
    }
}

