package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.entity.Employee;
import com.example.rqchallenge.employees.model.EmployeeWrapper;
import com.example.rqchallenge.employees.util.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmployeeService implements IEmployeeService {

    private final WebClient webClient;
    @Autowired
    public EmployeeService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.info("started getAllEmployees() in service");
        try {
            return webClient.get()
                    .uri("/employees")
                    .retrieve()
                    .bodyToMono(EmployeeWrapper.class).map(EmployeeWrapper::getData)
                    .block();
        } catch (
        WebClientResponseException e) {
            log.error("WebClientResponseException: Status code: {}, Response body: {}",
                e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to fetch employees from dummyjson API", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching employees", e);
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }
    @Override
    public Employee getEmployee(String id) {
        log.info("started getEmployee() in service");
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/employees/{id}").build(id))
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (
                WebClientResponseException e) {
            log.error("WebClientResponseException: Status code: {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to fetch employees from dummyjson API", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching employees", e);
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    @Override
    public Integer getHighestSalary() {
        log.info("started getHighestSalary() in service");
        try {
            return webClient.get()
                    .uri("/employees")
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .map(Employee::getEmployeeSalary)
                    .sort(Comparator.reverseOrder())
                    .next()
                    .defaultIfEmpty(0)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException: Status code: {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to fetch employees from the API", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching employees", e);
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    @Override
    public Employee createEmployee(Map<String, Object> employeeInput) {

        log.info("started createEmployees() in service");
        Employee employee = ConvertUtil.objectToEmployee(employeeInput);
        if (employee != null &&
                (employee.getEmployeeName() == null ||
                        employee.getEmployeeSalary() == null ||
                        employee.getEmployeeAge() == null)) {
            log.warn("Employee object is not null, but some fields are null: {}", employee);
            throw new RuntimeException("Invalid employee data");

        }

        try {
            return webClient.post()
                    .uri("/employees")
                    .bodyValue(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .doOnError(e -> log.error("Error during employee creation: {}", e.getMessage(), e))
                    .block();
        } catch (WebClientResponseException e){
            log.error("WebClientResponseException. Failed to create a new employee: statuscode: {}", e.getStatusCode());
            throw new RuntimeException("Failed to add the employee", e);
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployee(String id) {
        log.info("started deleteEmployee() with employee id: {}", id);
        try{
            webClient.delete()
                    .uri("/employee/{id}", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("Employee is deleted");
            return ResponseEntity.noContent().build();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Employee with id {} not found. Please check it again", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (WebClientResponseException e) {
            log.error("Failed to delete employee with id {}: status code: {}, Please check it again", id, e.getStatusCode());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
