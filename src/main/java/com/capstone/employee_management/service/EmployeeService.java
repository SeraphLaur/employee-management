package com.capstone.employee_management.service;

import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.dto.StatisticsResponseDto;
import com.capstone.employee_management.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    //Basic CRUD Operations for employees
    Page<EmployeeResponseDto> findAll(Pageable pageable);
    EmployeeResponseDto createEmployee(EmployeeRequestDto req);
    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto req);
    void deleteEmployee(Long id);

    //Search and filter options
    Page<EmployeeResponseDto> searchEmployees(String keyword, Pageable pageable);
    Page<EmployeeResponseDto> findByDepartment(String departmentName, Pageable pageable);
    StatisticsResponseDto getStatistics();

    //Methods for searching employee age (polymorphic methods)
    Page<EmployeeResponseDto> findByAge(Integer minAge, Integer maxAge, Pageable pageable);
    Page<EmployeeResponseDto> findByAge(Integer age, Pageable pageable);

}
