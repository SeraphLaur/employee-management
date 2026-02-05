package com.capstone.employee_management.repository;

import com.capstone.employee_management.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmployeeIdIgnoreCase(String employeeId);

    List<Employee> findByDepartment_NameIgnoreCase(String departmentName);
    List<Employee> findByNameContainingIgnoreCase(String nameKeyword);
}
