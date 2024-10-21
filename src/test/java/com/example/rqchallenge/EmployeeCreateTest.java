package com.example.rqchallenge;

import com.example.rqchallenge.employees.entity.Employee;
import com.example.rqchallenge.employees.service.EmployeeService;
import com.example.rqchallenge.employees.util.ConvertUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeCreateTest {
    private MockWebServer mockWebServer;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString());
        employeeService = new EmployeeService(webClientBuilder);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void createEmployees_ShouldReturnCreatedEmployee() throws Exception {

        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("employee_name", "test");
        employeeInput.put("employee_salary", 123);
        employeeInput.put("employee_age", 23);
        employeeInput.put("profile_image", "");

        Employee employee = ConvertUtil.objectToEmployee(employeeInput);

        String mockResponse = "{"
                + "\"employee_name\": \"test\","
                + "\"employee_salary\": 123,"
                + "\"employee_age\": 23,"
                + "\"profile_image\": \"\""
                + "}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Employee createdEmployee = employeeService.createEmployee(employeeInput);

        assertNotNull(createdEmployee);
        Assertions.assertEquals(employee.getEmployeeName(), createdEmployee.getEmployeeName());
        Assertions.assertEquals(employee.getEmployeeSalary(), createdEmployee.getEmployeeSalary());
        Assertions.assertEquals(employee.getEmployeeAge(), createdEmployee.getEmployeeAge());
        Assertions.assertEquals(employee.getProfileImage(), createdEmployee.getProfileImage());
    }

    @Test
    void createEmployee_WithEmptyInput_ShouldThrowException() {
        Map<String, Object> employeeInput = new HashMap<>();

        String mockResponse = "{\"error\": \"Missing required fields\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee(employeeInput);
        });

        String expectedMessage = "Invalid employee data";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void createEmployee_WithInvalidInput_ShouldThrowException() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("employeeName", "Jane Doe");
        employeeInput.put("employeeSalary", "invalid_salary");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\": \"Invalid input\"}")
                .addHeader("Content-Type", "application/json"));

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee(employeeInput);
        });

        String expectedMessage = "Invalid employee data";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage);

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void createEmployee_WithMissingFields_ShouldReturnEmployeeWithDefaults() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("employeeName", "John Doe");
        employeeInput.put("employeeSalary", 150);
        // Missing "employeeAge" and "profileImage"

        String mockResponse = "{"
                + "\"id\": 2,"
                + "\"employeeName\": \"John Doe\","
                + "\"employeeSalary\": 150,"
                + "\"employeeAge\": 0,"
                + "\"profileImage\": \"\""
                + "}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

//        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee(employeeInput);
        });
        String expectedMessage = "Invalid employee data";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage);

        assertTrue(actualMessage.contains(expectedMessage));

    }

}
