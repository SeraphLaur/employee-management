package com.capstone.employee_management.dto;

import java.math.BigDecimal;

public record StatisticsRecordDto(
        long totalEmployees,
        BigDecimal averageSalary,
        Double averageAge) {
}
