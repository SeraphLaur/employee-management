package com.capstone.employee_management.dto;

import com.capstone.employee_management.model.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequestDto(
        @NotBlank String employeeId,
        @NotBlank String name,
        @PastOrPresent LocalDate dateOfBirth,
        @NotBlank String departmentName,
        @PositiveOrZero BigDecimal salary
) {}