package com.capstone.employee_management.service;

import com.capstone.employee_management.dto.DepartmentRequestDto;
import com.capstone.employee_management.dto.DepartmentResponseDto;
import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.mapper.DepartmentMapper;
import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.repository.DepartmentRepository;
import com.capstone.employee_management.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeRepository employeeRepository;
    private final MessageSource messageSource;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper, EmployeeRepository employeeRepository, MessageSource messageSource) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.employeeRepository = employeeRepository;
        this.messageSource = messageSource;
    }

    public Page<DepartmentResponseDto> findAll(Pageable pageable) {

        return departmentRepository.findAll(pageable).map(departmentMapper::toResponse);
    }

    public DepartmentResponseDto createDepartment(DepartmentRequestDto req) {
        if(req.deptName() == null){
            throw new IllegalArgumentException(
                    messageSource.getMessage(
                            "department.cannot.be.null",
                            null,
                            Locale.getDefault()
                    )
            );
        }
        Optional<Department> newDepartment = departmentRepository.findByNameIgnoreCase(req.deptName());
        if(newDepartment.isPresent()){
            throw new IllegalArgumentException(
                    messageSource.getMessage(
                            "department.already.exists",
                            new Object[]{req.deptName()},
                            Locale.getDefault()
                    )
            );
        }

        Department toSaveDept = new Department();
        toSaveDept.setName(req.deptName());
        return departmentMapper.toResponse(departmentRepository.save(toSaveDept));
    }

    public DepartmentResponseDto updateDepartment(String deptName, DepartmentRequestDto req) {
        if(req.deptName() == null){
            throw new IllegalArgumentException(
                    messageSource.getMessage(
                            "department.cannot.be.null",
                            null,
                            Locale.getDefault()
                    )
            );
        }
        Department toUpdateDept = findDepartmentByName(deptName);

        Optional<Department> existingWithNewName = departmentRepository.findByNameIgnoreCase(req.deptName());
        if(existingWithNewName.isPresent() && !existingWithNewName.get().getId().equals(toUpdateDept.getId())){
            throw new IllegalArgumentException(
                    messageSource.getMessage(
                            "department.already.exists",
                            new Object[]{req.deptName()},
                            Locale.getDefault()
                    )
            );
        }
        toUpdateDept.setName(req.deptName());

        return departmentMapper.toResponse(departmentRepository.save(toUpdateDept));

    }

    public void deleteDepartment(String name) {
        if(name == null){
            throw new IllegalArgumentException(
                    messageSource.getMessage(
                            "department.cannot.be.null",
                            null,
                            Locale.getDefault()
                    )
            );
        }

        Department department = findDepartmentByName(name);

        long employeeCount = employeeRepository.countByDepartment(department);
        if (employeeCount > 0) {
            throw new IllegalStateException(
                    messageSource.getMessage(
                            "department.has.employees",
                            new Object[]{employeeCount},
                            Locale.getDefault()
                    )
            );
        }

        departmentRepository.delete(department);
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
