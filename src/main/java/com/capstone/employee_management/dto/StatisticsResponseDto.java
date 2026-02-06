package com.capstone.employee_management.dto;

import java.math.BigDecimal;

public record StatisticsResponseDto(
        long totalEmployees,
        BigDecimal averageSalary,
        Double averageAge) {
}
