package dk.trustworks.bimanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.client.RestDelegate;
import dk.trustworks.bimanager.dto.ReportDTO;
import dk.trustworks.bimanager.dto.User;
import dk.trustworks.bimanager.dto.Work;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

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

        for (Work work : restClient.getRegisteredWorkByMonth(Integer.parseInt(queryParameters.get("year").getFirst()), Integer.parseInt(queryParameters.get("month").getFirst()))) {
            ReportDTO reportDTO = null;
            for (ReportDTO newReportDTO : reportDTOs) {
                if (newReportDTO.getTaskUUID().equals(work.getTaskUUID()) && newReportDTO.getWorkerUUID().equals(work.getUserUUID())) {
                    reportDTO = newReportDTO;
                }
            }

            if (reportDTO == null) {
                reportDTO = new ReportDTO();
                if (work.getWorkDuration() > 0) reportDTOs.add(reportDTO);
                User user = restDelegate.getAllUsersMap().get(work.getUserUUID());
                reportDTO.setWorkerName(user.getFirstname() + " " + user.getLastname());
                reportDTO.setWorkerUUID(work.getUserUUID());

                Map<String, String> taskProjectClient = restClient.getTaskProjectClient(work.getTaskUUID());
                reportDTO.setClientName(taskProjectClient.get("clientname"));
                reportDTO.setProjectName(taskProjectClient.get("projectname"));
                reportDTO.setTaskName(taskProjectClient.get("taskname"));
                reportDTO.setTaskUUID(work.getTaskUUID());
            }

            reportDTO.setHours(reportDTO.getHours() + work.getWorkDuration());

            double workerRate = restClient.getTaskWorkerRate(work.getTaskUUID(), work.getUserUUID());
            reportDTO.setRate(workerRate);
            reportDTO.setSum(reportDTO.getHours() * reportDTO.getRate());
        }

        return reportDTOs;
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
