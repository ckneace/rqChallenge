package com.example.rqchallenge.employees.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private Long id;

    @JsonProperty(value = "employee_name")
    private String employeeName;

    @JsonProperty(value = "employee_salary")
    private Integer employeeSalary;

    @JsonProperty(value = "employee_age")
    private Integer employeeAge;

    private String profileImage;

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", employeeSalary=" + employeeSalary +
                ", employeeAge=" + employeeAge +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
