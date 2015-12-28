package dk.trustworks.invoicemanager.services;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.invoicemanager.client.RestClient;
import dk.trustworks.invoicemanager.dto.Client;
import dk.trustworks.invoicemanager.dto.Project;
import dk.trustworks.invoicemanager.persistence.InvoiceRepository;

import java.sql.SQLException;

/**
 * Created by hans on 17/03/15.
 */
public class InvoiceService extends DefaultLocalService {

    private InvoiceRepository invoiceRepository;

    public InvoiceService() {
        invoiceRepository = new InvoiceRepository();
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        String projectUUID = jsonNode.get("projectuuid").asText();
        int month = jsonNode.get("month").asInt();
        int year = jsonNode.get("year").asInt();

        //invoiceRepository.findByProjectUUIDAndMonthAndYear()

        RestClient restClient = new RestClient();
        Project project = restClient.getProjectByUUID(jsonNode.get("projectuuid").asText());
        Client client = restClient.getClientByUUID(project.getClientUUID());


        invoiceRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        invoiceRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return invoiceRepository;
    }

    @Override
    public String getResourcePath() {
        return "invoices";
    }
}
