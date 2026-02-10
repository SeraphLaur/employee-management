package com.capstone.employee_management.mapper;

import com.capstone.employee_management.dto.DepartmentRequestDto;
import com.capstone.employee_management.dto.DepartmentResponseDto;
import com.capstone.employee_management.model.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {
    public DepartmentResponseDto toResponse(Department d) {
        return new DepartmentResponseDto(
                d.getName()
        );
    }
    public Department toEntity(DepartmentRequestDto dto) {
        Department d = new Department();
        d.setName(dto.deptName());
        return d;
    }

    public void updateEntity(Department target, DepartmentRequestDto dto) {
        target.setName(dto.deptName());
    }
}
