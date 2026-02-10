package com.capstone.employee_management.service;

import com.capstone.employee_management.dto.DepartmentRequestDto;
import com.capstone.employee_management.dto.DepartmentResponseDto;
import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.mapper.DepartmentMapper;
import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public Page<DepartmentResponseDto> findAll(Pageable pageable) {

        return departmentRepository.findAll(pageable).map(departmentMapper::toResponse);
    }

    public DepartmentResponseDto createDepartment(DepartmentRequestDto req) {
        if(req.deptName() == null){
            throw new IllegalArgumentException("Department cannot be null");
        }
        Optional<Department> newDepartment = departmentRepository.findByNameIgnoreCase(req.deptName());
        if(newDepartment.isPresent()){
            throw new IllegalArgumentException("Department already exists");
        }

        Department toSaveDept = new Department();
        toSaveDept.setName(req.deptName());
        return departmentMapper.toResponse(departmentRepository.save(toSaveDept));
    }

    public DepartmentResponseDto updateDepartment(String deptName, DepartmentRequestDto req) {
        if(req.deptName() == null){
            throw new IllegalArgumentException("Department cannot be null");
        }
        boolean exists = departmentRepository.existsByNameIgnoreCase(deptName);

        if(exists){
            throw new IllegalArgumentException("Department already exists");
        }

        Department toUpdateDept = findDepartmentByName(deptName);
        toUpdateDept.setName(req.deptName());

        return departmentMapper.toResponse(departmentRepository.save(toUpdateDept));

    }

    public void deleteDepartment(DepartmentRequestDto req) {
        if(req.deptName() == null){
            throw new IllegalArgumentException("Department cannot be null");
        }
        if(!departmentRepository.existsByNameIgnoreCase(req.deptName())){
            throw new IllegalArgumentException("Department does not exists");
        }
        departmentRepository.delete(findDepartmentByName(req.deptName()));
    }

    //helper method, independent of the Employee Service interface
    protected Department findDepartmentByName(String name) {
        return departmentRepository.findByNameIgnoreCase(name)
                .orElseThrow(()-> new EntityNotFoundException("Department not found with name: " + name));

    }
}
