package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.client.RestDelegate;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 21/05/15.
 */
public class ReportService extends DefaultLocalService {

    private final RestClient restClient;
    private final RestDelegate restDelegate;

    public ReportService() {
        restClient = new RestClient();
        restDelegate = RestDelegate.getInstance();
    }

    public ArrayList<ReportDTO> findByTaskUUIDAndUserUUID(Map<String, Deque<String>> queryParameters) {
        ArrayList<ReportDTO> reportDTOs = new ArrayList<>();
        List<Client> clients = restDelegate.getAllClientsGraph();

        for (Work work : restClient.getRegisteredWorkByMonth(Integer.parseInt(queryParameters.get("year").getFirst()), Integer.parseInt(queryParameters.get("month").getFirst()))) {
            ReportDTO reportDTO = null;
            for (ReportDTO newReportDTO : reportDTOs) {
                if (newReportDTO.getTaskUUID().equals(work.getTaskUUID()) && newReportDTO.getWorkerUUID().equals(work.getUserUUID())) {
                    reportDTO = newReportDTO;
                }
            }

            if (reportDTO == null) {
                reportDTO = new ReportDTO();
                if (work.getWorkDuration() > 0) {
                    reportDTOs.add(reportDTO);
                //} else {
                    //continue;
                //}
            }

            User user = restDelegate.getAllUsersMap().get(work.getUserUUID());
            reportDTO.setWorkerName(user.getFirstname() + " " + user.getLastname());
            reportDTO.setWorkerUUID(work.getUserUUID());

            Map<String, String> taskProjectClient = extractNames(clients, work.getTaskUUID(), work.getUserUUID());
            reportDTO.setClientName(taskProjectClient.get("clientname"));
            reportDTO.setProjectName(taskProjectClient.get("projectname"));
            reportDTO.setTaskName(taskProjectClient.get("taskname"));
            reportDTO.setTaskUUID(work.getTaskUUID());

            reportDTO.setHours(reportDTO.getHours() + work.getWorkDuration());

            double workerRate = Double.parseDouble(taskProjectClient.get("rate"));//restClient.getTaskWorkerRate(work.getTaskUUID(), work.getUserUUID());
            reportDTO.setRate(workerRate);
            reportDTO.setSum(reportDTO.getHours() * reportDTO.getRate());
        }

        return reportDTOs;
    }

    private HashMap<String, String> extractNames(List<Client> clients, String taskUUID, String userUUID) {
        HashMap<String, String> result = new HashMap<>();
        for (Client client : clients) {
            for (Project project : client.projects) {
                for (Task task : project.getTasks()) {
                    if(task.getUUID().equals(taskUUID)) {
                        for (TaskWorkerConstraint taskWorkerConstraint : task.getTaskWorkerConstraints()) {
                            if(taskWorkerConstraint.getUserUUID().equals(userUUID)) {
                                result.put("taskname", task.getName());
                                result.put("projectname", project.getName());
                                result.put("clientname", client.name);
                                result.put("rate", taskWorkerConstraint.getPrice()+"");
                                return result;
                            }
                        }

                    }
                }
            }
        }
        return null;
    }

    @Override
    public GenericRepository getGenericRepository() {
        return null;
    }

    @Override
    public String getResourcePath() {
        return "reports";
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
