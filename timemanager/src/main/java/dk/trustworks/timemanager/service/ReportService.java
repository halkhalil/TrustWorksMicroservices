package dk.trustworks.timemanager.service;

import dk.trustworks.framework.model.*;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import net.sf.cglib.proxy.Enhancer;

import javax.sql.DataSource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by hans on 17/03/15.
 */
public class ReportService {

    private WorkService workService;
    private UserService userService;
    private ClientService clientService;

    public ReportService() {
    }

    public ReportService(DataSource ds) {
        workService = new WorkService(ds);
        userService = new UserService();
        clientService = new ClientService();
    }

    public static ReportService getInstance(DataSource ds) {
        ReportService service = new ReportService(ds);
        return (ReportService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.user")
    public ArrayList<Report> findByYearAndMonth(int year, int month) {
        ArrayList<Report> reportDTOs = new ArrayList<>();
        List<Client> clients =  clientService.findAll("project/task/taskworkerconstraint");//restDelegate.getAllClientsGraph();
        Map<String, User> usersMap = userService.findAll().stream().collect(Collectors.toMap(User::getUuid, Function.identity()));
        System.out.println("clients.size() = " + clients.size());
        for (Work work : workService.findByYearAndMonth(year, month)) { // restClient.getRegisteredWorkByMonth(Integer.parseInt(queryParameters.get("year").getFirst()), Integer.parseInt(queryParameters.get("month").getFirst()))) {
            Report reportDTO = null;
            for (Report newReport : reportDTOs) {
                if (newReport.taskUUID.equals(work.taskuuid) && newReport.workerUUID.equals(work.useruuid)) {
                    reportDTO = newReport;
                }
            }

            if (reportDTO == null) {
                reportDTO = new Report();
                if (work.workduration > 0) reportDTOs.add(reportDTO);
                User user = usersMap.get(work.useruuid);//restDelegate.getAllUsersMap().get(work.useruuid);
                if(user==null) System.out.println("reportDTO = " + work.useruuid);
                reportDTO.workerName = user.firstname + " " + user.lastname;
                reportDTO.workerUUID = work.useruuid;

                Map<String, String> taskProjectClient = extractNames(clients, work.taskuuid, work.useruuid);
                reportDTO.clientName = taskProjectClient.get("clientname");
                reportDTO.projectName = taskProjectClient.get("projectname");
                reportDTO.taskName = taskProjectClient.get("taskname");
                reportDTO.taskUUID = work.taskuuid;
                double workerRate = Double.parseDouble(taskProjectClient.get("rate"));//restClient.getTaskWorkerRate(work.getTaskUUID(), work.getUserUUID());
                reportDTO.rate = workerRate;

            }

            reportDTO.hours = reportDTO.hours + work.workduration;

            reportDTO.sum = reportDTO.hours * reportDTO.rate;
        }

        return reportDTOs;
    }

    private HashMap<String, String> extractNames(List<Client> clients, String taskUUID, String userUUID) {
        HashMap<String, String> result = new HashMap<>();
        for (Client client : clients) {
            for (Project project : client.projects) {
                for (Task task : project.tasks) {
                    if(task.uuid.equals(taskUUID)) {
                        for (TaskWorkerConstraint taskWorkerConstraint : task.taskworkerconstraints) {
                            if(taskWorkerConstraint.useruuid.equals(userUUID)) {
                                result.put("taskname", task.name);
                                result.put("projectname", project.name);
                                result.put("clientname", client.name);
                                result.put("rate", taskWorkerConstraint.price+"");
                                return result;
                            }
                        }
                        result.put("taskname", task.name);
                        result.put("projectname", project.name);
                        result.put("clientname", client.name);
                        result.put("rate", "0.0");
                        return result;
                    }
                }
            }
        }
        return null;
    }

}
