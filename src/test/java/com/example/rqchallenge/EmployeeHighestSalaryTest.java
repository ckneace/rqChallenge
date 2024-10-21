package com.example.rqchallenge;


import com.example.rqchallenge.employees.service.EmployeeService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeHighestSalaryTest {
    private MockWebServer mockWebServer;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        mockWebServer = new MockWebServer();
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString());

        employeeService = new EmployeeService(webClientBuilder);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getHighestSalary_ShouldReturnMaxSalary() {
        String mockResponse = "["
                + "{ \"id\": \"1\", \"employee_name\": \"Alice\", \"employee_salary\": 50000, \"employee_age\": 30, \"profile_image\": \"\" },"
                + "{ \"id\": \"2\", \"employee_name\": \"Bob\", \"employee_salary\": 75000, \"employee_age\": 28, \"profile_image\": \"\" },"
                + "{ \"id\": \"3\", \"employee_name\": \"Charlie\", \"employee_salary\": 60000, \"employee_age\": 35, \"profile_image\": \"\" }"
                + "]";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Integer highestSalary = employeeService.getHighestSalary();

        assertNotNull(highestSalary);
        assertEquals(75000, highestSalary);
    }

    @Test
    void getHighestSalary_ShouldReturnZero_WhenNoEmployees() {
        String mockResponse = "[]";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Integer highestSalary = employeeService.getHighestSalary();

        assertNotNull(highestSalary);
        assertEquals(0, highestSalary);
    }

    @Test
    void getHighestSalary_ShouldReturnSalaryOfSingleEmployee() {
        String mockResponse = "["
                + "{ \"id\": \"1\", \"employee_name\": \"Alice\", \"employee_salary\": 50000, \"employee_age\": 30, \"profile_image\": \"\" }"
                + "]";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Integer highestSalary = employeeService.getHighestSalary();

        assertNotNull(highestSalary);
        assertEquals(50000, highestSalary);
    }

    @Test
    void getHighestSalary_ShouldHandleNegativeSalaries() {
        String mockResponse = "["
                + "{ \"id\": \"1\", \"employee_name\": \"Alice\", \"employee_salary\": -10000, \"employee_age\": 30, \"profile_image\": \"\" },"
                + "{ \"id\": \"2\", \"employee_name\": \"Bob\", \"employee_salary\": 20000, \"employee_age\": 28, \"profile_image\": \"\" },"
                + "{ \"id\": \"3\", \"employee_name\": \"Charlie\", \"employee_salary\": -5000, \"employee_age\": 35, \"profile_image\": \"\" }"
                + "]";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Integer highestSalary = employeeService.getHighestSalary();

        assertNotNull(highestSalary);
        assertEquals(20000, highestSalary);
    }

    @Test
    void getHighestSalary_ShouldHandleInvalidJsonResponse() {
        String invalidMockResponse = "{ \"invalid\": \"data\" }";

        mockWebServer.enqueue(new MockResponse()
                .setBody(invalidMockResponse)
                .addHeader("Content-Type", "application/json"));

        try {
            employeeService.getHighestSalary();
        } catch (RuntimeException e) {
            assertEquals("An unexpected error occurred", e.getMessage());
        }
    }
}