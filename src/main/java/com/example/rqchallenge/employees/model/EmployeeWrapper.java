package com.example.rqchallenge.employees.model;

import com.example.rqchallenge.employees.entity.Employee;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeWrapper {
    private List<Employee> data;
}
