package dk.trustworks.employeemanager.services;

import dk.trustworks.employeemanager.dto.Employee;
import dk.trustworks.employeemanager.persistence.EmployeeRepository;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by hans on 17/07/16.
 */
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(DataSource ds) {
        employeeRepository = new EmployeeRepository(ds);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee find(String uuid) {
        return employeeRepository.find(uuid);
    }
}
