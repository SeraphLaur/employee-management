package com.capstone.employee_management.service;

import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }


    public Department create(String name) {
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Department already exists: " + name);
        }
        Department d = new Department();
        d.setName(name);
        return departmentRepository.save(d);
    }


    @Transactional(readOnly = true)
    public Department getById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found: id=" + id));
    }


    public Department getOrCreateByName(String name) {
        return departmentRepository.findByNameContainingIgnoreCase(name)
                .orElseGet(() -> departmentRepository.save(new Department()));

    }


    public Department rename(Long id, String newName) {
        Department dept = getById(id);
        if (departmentRepository.existsByNameIgnoreCase(newName)) {
            throw new IllegalArgumentException("Department already exists: " + newName);
        }
        dept.setName(newName);
        return departmentRepository.save(dept);
    }


    @Transactional(readOnly = true)
    public List<Department> listAll() {
        return departmentRepository.findAll();
    }


    public void delete(Long id) {

        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department not found: id=" + id);
        }
        departmentRepository.deleteById(id);
    }
}
