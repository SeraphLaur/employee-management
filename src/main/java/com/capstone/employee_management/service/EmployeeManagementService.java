package com.capstone.employee_management.service;

import com.capstone.employee_management.mapper.EmployeeMapper;
import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.model.Employee;
import com.capstone.employee_management.repository.DepartmentRepository;
import com.capstone.employee_management.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
@Transactional
public abstract class EmployeeManagementService implements EmployeeService {
    protected final EmployeeRepository employeeRepository;
    protected final DepartmentRepository departmentRepository;
    protected final EmployeeMapper employeeMapper;
    protected final MessageSource messageSource;

    public EmployeeManagementService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,  MessageSource messageSource ) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toResponse);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> searchEmployees(String keyword, Pageable pageable) {
        return employeeRepository.searchEmployees(keyword, pageable)
                .map(employeeMapper::toResponse);
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto req) {
        if(req.employeeId()  !=null && !req.employeeId().isBlank() &&
                employeeRepository.existsByEmployeeIdIgnoreCase(req.employeeId())) {
            throw new IllegalArgumentException(
                    messageSource.getMessage(
                            "employee.already.exists",
                            new Object[]{req.employeeId()},
                            Locale.getDefault()
                    )
            );
//        throw new IllegalArgumentException("Employee already exists with id: " + req.employeeId());
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
    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto req) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(()->
                        new EntityNotFoundException(messageSource.getMessage(
                                "employee.not.found.id",
                                new Object[]{id},
                                Locale.getDefault()
                        )
                        ));

        if(req.employeeId()  !=null && !req.employeeId().isBlank()) {
            boolean idTaken = employeeRepository.existsByEmployeeIdIgnoreCase(req.employeeId());
            boolean sameAsCurrent = req.employeeId().equalsIgnoreCase(existing.getEmployeeId());
            if(idTaken && !sameAsCurrent) {
                throw new IllegalArgumentException(
                        messageSource.getMessage(
                                "employee.id.taken.or.same",
                                null,
                                Locale.getDefault()
                        )
                );

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
    @Override
    public void deleteEmployee(Long id) {
        if(!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    messageSource.getMessage(
                            "employee.not.found.id",
                            new Object[]{id},
                            Locale.getDefault()
                    )
            );
        }
        employeeRepository.deleteById(id);
    }
    //helper method, independent of the Employee Service interface
    protected Department findDepartmentByName(String name) {
        return departmentRepository.findByNameIgnoreCase(name)
                .orElseThrow(()-> new EntityNotFoundException(
                        messageSource.getMessage(
                                "department.not.found.name",
                                new Object[]{name},
                                Locale.getDefault()
                        )
                ));

    }

 }



