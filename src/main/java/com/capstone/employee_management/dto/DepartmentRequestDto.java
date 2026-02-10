package com.capstone.employee_management.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequestDto(
        @NotBlank String deptName
){}
