package com.capstone.employee_management.service;

import com.capstone.employee_management.dto.EmployeeMapper;
import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.model.Employee;
import com.capstone.employee_management.repository.DepartmentRepository;
import com.capstone.employee_management.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

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

    public EmployeeResponseDto findById(Long id) {
        Employee e = employeeRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Employee not found with id: " + id));
    return employeeMapper.toResponse(e);
    }


    public EmployeeResponseDto createEmployee(EmployeeRequestDto req) {
        if(req.employeeId()  !=null && !req.employeeId().isBlank() &&
                employeeRepository.existsByEmployeeIdIgnoreCase(req.employeeId())) {
        throw new EntityNotFoundException("Employee already exists with id: " + req.employeeId());
        }

        Department dept = findDepartmentByName(req.departmentName()) ;
        Employee saved = employeeRepository.save(employeeMapper.toEntity(req, dept));
        return employeeMapper.toResponse(saved);


    }

    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto req) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Employee not found with id: " + id));

        if(req.employeeId()  !=null && !req.employeeId().isBlank()) {
            boolean idTaken = employeeRepository.existsByEmployeeIdIgnoreCase(existing.getEmployeeId());
            boolean sameAsCurrent = req.employeeId().equalsIgnoreCase(existing.getEmployeeId());
            if(idTaken && !sameAsCurrent) {
                throw new IllegalArgumentException("Employee already exists or you entered the previous ID.");

            }
        }
        Department dept = findDepartmentByName(req.departmentName());
        employeeMapper.updateEntity(existing, req, dept);
        Employee updated = employeeRepository.save(existing);
        return employeeMapper.toResponse(updated);

    }

    public void deleteEmployee(Long id) {
        if(!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private Department findDepartmentByName(String name) {
        return departmentRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(()-> new EntityNotFoundException("Department not found with name: " + name));

    }
}
