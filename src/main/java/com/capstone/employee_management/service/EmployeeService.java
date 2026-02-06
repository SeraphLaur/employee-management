package com.capstone.employee_management.service;

import com.capstone.employee_management.dto.EmployeeMapper;
import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.dto.StatisticsResponseDto;
import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.model.Employee;
import com.capstone.employee_management.repository.DepartmentRepository;
import com.capstone.employee_management.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> findAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponse)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> searchEmployees(String keyword) {
        return employeeRepository.searchEmployees(keyword).stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

//    public EmployeeResponseDto findById(Long id) {
//        Employee e = employeeRepository.findById(id)
//                .orElseThrow(()-> new EntityNotFoundException("Employee not found with id: " + id));
//    return employeeMapper.toResponse(e);
//    }


    public EmployeeResponseDto createEmployee(EmployeeRequestDto req) {
        if(req.employeeId()  !=null && !req.employeeId().isBlank() &&
                employeeRepository.existsByEmployeeIdIgnoreCase(req.employeeId())) {
        throw new IllegalArgumentException("Employee already exists with id: " + req.employeeId());
        }


        boolean exists = departmentRepository.existsByNameIgnoreCase(req.departmentName()) ;

        if(exists) {
            Department dept = findDepartmentByName(req.departmentName()) ;
            Employee saved = employeeRepository.save(employeeMapper.toEntity(req, dept));
            return employeeMapper.toResponse(saved);
        }

        Department newDepartment = new Department();
        newDepartment.setName(req.departmentName());
        departmentRepository.save(newDepartment);

        return employeeMapper.toResponse(employeeRepository.save(employeeMapper.toEntity(req, newDepartment)));




    }

    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto req) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Employee not found with id: " + id));

        if(req.employeeId()  !=null && !req.employeeId().isBlank()) {
            boolean idTaken = employeeRepository.existsByEmployeeIdIgnoreCase(req.employeeId());
            boolean sameAsCurrent = req.employeeId().equalsIgnoreCase(existing.getEmployeeId());
            if(idTaken && !sameAsCurrent) {
                throw new IllegalArgumentException("Employee already exists or you entered the previous ID.");

            }
        }

        boolean exists = departmentRepository.existsByNameIgnoreCase(req.departmentName()) ;

        if(exists) {
            Department dept = findDepartmentByName(req.departmentName());
            employeeMapper.updateEntity(existing, req, dept);
            return employeeMapper.toResponse(employeeRepository.save(existing));
        }

        Department newDepartment = new Department();
        newDepartment.setName(req.departmentName());
        departmentRepository.save(newDepartment);

        employeeMapper.updateEntity(existing, req, newDepartment);

        return employeeMapper.toResponse(employeeRepository.save(existing));


    }

    public void deleteEmployee(Long id) {
        if(!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private Department findDepartmentByName(String name) {
        return departmentRepository.findByNameIgnoreCase(name)
                .orElseThrow(()-> new EntityNotFoundException("Department not found with name: " + name));

    }

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

    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> findByDepartment(String departmentName, Pageable pageable) {
        return employeeRepository.findByDepartment_NameIgnoreCase(departmentName, pageable)
                .map(employeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> findByAgeRange(int minAge, int maxAge, Pageable pageable) {
        return employeeRepository.findAgeBetween(minAge, maxAge, pageable)
                .map(employeeMapper::toResponse);
    }
}

