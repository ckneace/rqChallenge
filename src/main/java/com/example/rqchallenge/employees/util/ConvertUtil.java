package com.example.rqchallenge.employees.util;

import com.example.rqchallenge.employees.entity.Employee;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ConvertUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     *
     * @param map details to convert objects to Employee using objectMapper
     * @return the Employee object
     */
    public static Employee objectToEmployee(Map<String, Object> map) {
        return objectMapper.convertValue(map, Employee.class);
    }
}
