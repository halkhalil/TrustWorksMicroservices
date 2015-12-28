package dk.trustworks.timemanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;

import java.sql.SQLException;

/**
 * Created by hans on 15/05/15.
 */
public class TaskWeekViewRepository extends GenericRepository {

    public TaskWeekViewRepository() {
        super();
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {

    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {

    }

}