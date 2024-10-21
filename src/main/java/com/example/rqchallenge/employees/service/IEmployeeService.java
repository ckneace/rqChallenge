package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.entity.Employee;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {

    /**
     * Fetches all employees from the external service.
     *
     * @return a list of all employees.
     */
    List<Employee> getAllEmployees();
    /**
     * Creates a new employee based on the provided input data.
     *
     * @param employeeInput a map containing the details of the employee to be created.
     * @return the created employee object.
     */
    Employee createEmployee(Map<String, Object> employeeInput);
    /**
     * Deletes an employee with the given ID.
     *
     * @param id the ID of the employee to be deleted.
     * @return a response entity indicating the result of the deletion operation.
     */
    ResponseEntity<String> deleteEmployee(String id);
    /**
     * Fetches a specific employee by their ID.
     *
     * @param id the ID of the employee to be fetched.
     * @return the employee object with the given ID.
     */
    Employee getEmployee(String id);

    /**
     * Fetches the highest salary among all employees.
     *
     * @return the highest salary as an integer. Returns 0 if no employees are found.
     */
    Integer getHighestSalary();
}
