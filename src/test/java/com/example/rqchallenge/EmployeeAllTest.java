package com.example.rqchallenge;

import com.example.rqchallenge.employees.entity.Employee;
import com.example.rqchallenge.employees.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class EmployeeAllTest {

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
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getAllEmployees_ShouldReturnMockedEmployees() throws InterruptedException {
        String mockResponseBody = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"employee_name\": \"John Doe\",\n" +
                "      \"employee_salary\": 50000,\n" +
                "      \"employee_age\": 30,\n" +
                "      \"profile_image\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"employee_name\": \"Jane Smith\",\n" +
                "      \"employee_salary\": 60000,\n" +
                "      \"employee_age\": 25,\n" +
                "      \"profile_image\": \"\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)
                .addHeader("Content-Type", "application/json"));

        List<Employee> employees = employeeService.getAllEmployees();

        System.out.println("hola"+employees.get(0).getEmployeeName());

        assertThat(employees).isNotNull();
        assertThat(employees.size()).isEqualTo(2);
        assertThat(employees.get(0).getEmployeeName()).isEqualTo("John Doe");
        assertThat(employees.get(1).getEmployeeName()).isEqualTo("Jane Smith");
    }
    @Test
    void getAllEmployees_ShouldHandleNotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertTrue(exception.getMessage().contains("Failed to fetch employees"));
    }

    @Test
    void getAllEmployees_ShouldHandleServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertTrue(exception.getMessage().contains("Failed to fetch employees"));
    }

    @Test
    void getAllEmployees_ShouldHandleEmptyResponse() {
        String mockResponseBody = "{\n" +
                "  \"data\": []\n" +
                "}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)
                .addHeader("Content-Type", "application/json"));

        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertTrue(employees.isEmpty());
    }

    @Test
    void getAllEmployees_ShouldHandleMalformedJson() {
        String mockResponse = "{ malformed json }";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getAllEmployees();
        });

        assertTrue(exception.getMessage().contains("An unexpected error occurred"));
    }

}
