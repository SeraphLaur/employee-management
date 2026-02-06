package com.capstone.employee_management.repository;

import com.capstone.employee_management.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
