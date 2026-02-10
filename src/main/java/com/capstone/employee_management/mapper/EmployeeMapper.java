package com.capstone.employee_management.mapper;

import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeResponseDto toResponse(Employee e) {
        return new EmployeeResponseDto(
                e.getId(),
                e.getEmployeeId(),
                e.getName(),
                e.getDateOfBirth(),
                e.getDepartment() != null ? e.getDepartment().getName() : null,
                e.getSalary(),
                e.getAge()
        );
    }

    public Employee toEntity(EmployeeRequestDto dto, Department dept) {
        Employee e = new Employee();
        e.setEmployeeId(dto.employeeId());
        e.setName(dto.name());
        e.setDateOfBirth(dto.dateOfBirth());
        e.setDepartment(dept);
        e.setSalary(dto.salary());
        return e;
    }

    public void updateEntity(Employee target, EmployeeRequestDto dto, Department dept) {
        target.setEmployeeId(dto.employeeId());
        target.setName(dto.name());
        target.setDateOfBirth(dto.dateOfBirth());
        target.setDepartment(dept);
        target.setSalary(dto.salary());
    }
}
