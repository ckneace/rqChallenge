package com.example.rqchallenge.employees.controller;

import com.example.rqchallenge.employees.entity.Employee;
import com.example.rqchallenge.employees.service.IEmployeeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("employee")
@Slf4j
@AllArgsConstructor
public class EmployeeController implements IEmployeeController {

    IEmployeeService iEmployeeService;
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        log.info("started getAllEmployees() getting all the Employees");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(iEmployeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return null;
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.info("started getEmployeeId() get Employee with id: {}", id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(iEmployeeService.getEmployee(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("started getHighestSalaryOfEmployees() to grab the highest salary");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(iEmployeeService.getHighestSalary());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return null;
    }

    @Override
    public ResponseEntity<Object> createEmployee(Map<String, Object> employeeInput) {
        try{
            log.info("started createEmployee() to create the new employee");
            Employee employee= iEmployeeService.createEmployee(employeeInput);
            return ResponseEntity.status(HttpStatus.CREATED).body(employee);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create employee, Please check it again");
        }

    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
            log.info("started createEmployee()");
            return iEmployeeService.deleteEmployee(id);
    }
}
