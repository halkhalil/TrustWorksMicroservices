package dk.trustworks.invoicemanager.services;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.invoicemanager.persistence.ProductLineRepository;

import java.sql.SQLException;

/**
 * Created by hans on 17/03/15.
 */
public class ProductLineService extends DefaultLocalService {

    private ProductLineRepository productLineRepository;

    public ProductLineService() {
        productLineRepository = new ProductLineRepository();
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        productLineRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        productLineRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return productLineRepository;
    }

    @Override
    public String getResourcePath() {
        return "productlines";
    }
}
