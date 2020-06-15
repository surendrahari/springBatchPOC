package edu.core.batch3;

import edu.core.exception.ProcessNonRetriableException;
import edu.core.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Predicate;

@RestController
public class DBAccessAPI {

    private Predicate<Employee> testCondition = item -> item.getId() % 100 == 0;
    @Autowired
    private EmployeeRepo employeeRepo;

    @PostMapping("/api/employee")
    public Employee processEmployee(@RequestBody Employee employee) throws Exception {
        System.out.println("\t\tBegin getEmployee(DB) ...........");

        //validate
        if (testCondition.test(employee)) {
            throw new ProcessNonRetriableException("DB call fails" + employee.getId());
        }

        //DB Call & Process
        employee.setName(employee.getName().toUpperCase());

        System.out.println("\t\tEnd getEmployee(DB) ...........");
        return employee;
    }

    @GetMapping("/api/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }
}
