package com.example.rqchallenge;

import com.example.rqchallenge.employees.entity.Employee;
import com.example.rqchallenge.employees.service.EmployeeService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmployeeByIdTest {
    private MockWebServer mockWebServer;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString());

        employeeService = new EmployeeService(webClientBuilder);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getEmployee_ShouldReturnEmployee_WhenEmployeeExists() throws Exception {
        String employeeId = "123";
        String mockResponse = "{ \"id\": 123, \"employee_name\": \"John Doe\", \"employee_salary\": 50000, \"employee_age\": 30, \"profile_image\": \"\" }";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Employee employee = employeeService.getEmployee(employeeId);

        assertNotNull(employee);
        assertEquals(123, employee.getId());
        assertEquals("John Doe", employee.getEmployeeName());
        assertEquals(50000, employee.getEmployeeSalary());
        assertEquals(30, employee.getEmployeeAge());
    }

    @Test
    void getEmployee_ShouldThrowException_WhenEmployeeNotFound() {
        String employeeId = "999";
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(RuntimeException.class, () -> {
            employeeService.getEmployee(employeeId);
        });
    }

    @Test
    void getEmployee_ShouldThrowException_WhenServerError() {
        String employeeId = "123";
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(RuntimeException.class, () -> {
            employeeService.getEmployee(employeeId);
        });
    }
}