package dk.trustworks.employeemanager.services;

import dk.trustworks.employeemanager.dto.Contract;
import dk.trustworks.employeemanager.dto.StatusPeriod;
import dk.trustworks.employeemanager.persistence.StatusPeriodRepository;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 17/07/16.
 */
public class StatusPeriodService {

    private static final Logger logger = LoggerFactory.getLogger(StatusPeriodService.class);
    private final StatusPeriodRepository statusPeriodRepository;

    public StatusPeriodService(DataSource ds) {
        statusPeriodRepository = new StatusPeriodRepository(ds);
    }

    public Map<String, StatusPeriod[]> findAllByPeriod(LocalDate fromDate, LocalDate toDate) {
        Interval interval = new Interval(fromDate.toDateTimeAtStartOfDay(), toDate.toDateTimeAtStartOfDay());

        List<Contract> contractsOnStartDate = statusPeriodRepository.findAllByDate(fromDate);
        logger.debug("Contracts start of year: "+contractsOnStartDate.size());

        List<Contract> allContractsWithHistoryDuringPeriod = statusPeriodRepository.findAllWithHistoryDuringPeriod(fromDate, toDate);
        logger.debug("Contracts during year: "+allContractsWithHistoryDuringPeriod.size());

        int months = Months.monthsIn(interval).getMonths();
        logger.debug("months: "+months);

        Map<String, StatusPeriod[]> employeeStatusPeriods = new HashMap<>();
        logger.debug("New map");

        Set<String> listOfEmployeeUUID = contractsOnStartDate.stream().map(Contract::getEmpuuid).collect(Collectors.toCollection(TreeSet::new));
        logger.debug("New Set: "+listOfEmployeeUUID.size());

        listOfEmployeeUUID.addAll(allContractsWithHistoryDuringPeriod.stream().map(Contract::getEmpuuid).collect(Collectors.toList()));
        logger.debug("Added employees: "+listOfEmployeeUUID.size());

        for (int month = 0; month < months; month++) {
            for (String empuuid : listOfEmployeeUUID) {
                if(month == 0) {
                    logger.debug("First iteration...");
                    employeeStatusPeriods.put(empuuid, new StatusPeriod[months]);
                    contractsOnStartDate.stream().filter(contract -> contract.getEmpuuid().equals(empuuid)).forEach(contract -> {
                        logger.debug("Initial contract found: "+empuuid);
                        StatusPeriod statusPeriod = new StatusPeriod(empuuid, fromDate, contract.getSalary(), contract.getHours(), contract.getStatus());
                        employeeStatusPeriods.get(empuuid)[0] = statusPeriod;
                    });
                } else {
                    StatusPeriod previousStatusPeriod = employeeStatusPeriods.get(empuuid)[month - 1];
                    employeeStatusPeriods.get(empuuid)[month] = (previousStatusPeriod == null)?null:new StatusPeriod(previousStatusPeriod.getEmployeeUUID(), fromDate.plusMonths(month), previousStatusPeriod.getSalary(), previousStatusPeriod.getHours(), previousStatusPeriod.getStatus());
                    for (Contract contract : allContractsWithHistoryDuringPeriod) {
                        if(contract.getEmpuuid().equals(empuuid)) {
                            LocalDate localDate = LocalDate.fromDateFields(contract.getValiddate());
                            if((localDate.getYear() == fromDate.plusMonths(month).getYear()) && (localDate.getMonthOfYear() == fromDate.plusMonths(month).getMonthOfYear())) {
                                logger.debug("Contract update found in " + Month.of(month) + ": " + empuuid);
                                StatusPeriod statusPeriod = new StatusPeriod(empuuid, localDate, contract.getSalary(), contract.getHours(), contract.getStatus());
                                employeeStatusPeriods.get(empuuid)[month] = statusPeriod;
                            }
                        }
                    }
                }
            }
        }
        return employeeStatusPeriods;
    }
}
