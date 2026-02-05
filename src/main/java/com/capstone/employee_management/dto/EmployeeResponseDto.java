package com.capstone.employee_management.dto;

import com.capstone.employee_management.model.Department;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponseDto(
        Long id,
        String employeeCode,
        String name,
        LocalDate dateOfBirth,
        String department,
        BigDecimal salary,
        int age
) {}