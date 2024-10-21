package com.example.rqchallenge;

import com.example.rqchallenge.employees.service.EmployeeService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class EmployeeDeleteTest {
    private MockWebServer mockWebServer;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() throws IOException {

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString());
        employeeService = new EmployeeService(webClientBuilder);
    }

    @AfterEach
    void tearDown() throws IOException {

        mockWebServer.shutdown();
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent_WhenEmployeeExists() {
        String employeeId = "123";

        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        ResponseEntity<String> response = employeeService.deleteEmployee(employeeId);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteEmployee_ShouldReturnNotFound_WhenEmployeeDoesNotExist() {
        String employeeId = "999";

        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        ResponseEntity<String> response = employeeService.deleteEmployee(employeeId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteEmployee_ShouldReturnInternalServerError_WhenServerFails() {
        String employeeId = "123";

        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        ResponseEntity<String> response = employeeService.deleteEmployee(employeeId);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
