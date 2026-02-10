package com.capstone.employee_management.service;

import com.capstone.employee_management.mapper.EmployeeMapper;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.dto.StatisticsResponseDto;
import com.capstone.employee_management.repository.DepartmentRepository;
import com.capstone.employee_management.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Transactional
public class EmployeeTransactionService extends EmployeeManagementService {

    public EmployeeTransactionService( EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, DepartmentRepository departmentRepository) {
        super(departmentRepository, employeeRepository, employeeMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> findByDepartment(String departmentName, Pageable pageable) {
        return employeeRepository.findByDepartment_NameIgnoreCase(departmentName, pageable)
                .map(employeeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsResponseDto getStatistics() {
        long totalEmployees = employeeRepository.count();
        BigDecimal averageSalary = employeeRepository.findAveSalary();
        Double averageAge = employeeRepository.findAveAge();

        return new StatisticsResponseDto(
                totalEmployees,
                averageSalary != null ? averageSalary : BigDecimal.ZERO,
                averageAge != null ? averageAge : 0.0
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> findByAge(Integer minAge, Integer maxAge, Pageable pageable) {
        if(minAge==null || maxAge==null) {
            throw new IllegalArgumentException("minAge and maxAge cannot be null");
        }
        if(minAge<=0 || maxAge<=0) {
            throw new IllegalArgumentException("minAge and maxAge cannot be zero or negative");
        }
        if(minAge > maxAge) {
            int temp = minAge;
            minAge = maxAge;
            maxAge = temp;
        }
        return employeeRepository.findAgeBetween(minAge, maxAge, pageable)
                .map(employeeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> findByAge(Integer age, Pageable pageable) {
        if(age==null || age<=0) {
            throw new IllegalArgumentException("age cannot be null or negative");
        }
        return employeeRepository.findAgeBetween(age, age, pageable)
                .map(employeeMapper::toResponse);
    }
}

