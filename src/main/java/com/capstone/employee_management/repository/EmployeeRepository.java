package com.capstone.employee_management.repository;

import com.capstone.employee_management.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmployeeIdIgnoreCase(String employeeId);

    //display a list of employees based on their department
    Page<Employee> findByDepartment_NameIgnoreCase(String departmentName, Pageable pageable);

    //for the searching of the name of employee
    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.department.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> searchEmployees(@Param("keyword") String keyword);

    //find average of the salary of all employees
    @Query(value = "SELECT AVG(e.salary) from employee e", nativeQuery = true)
    BigDecimal findAveSalary();

    //find average of the age of all the employees
    @Query(value = "SELECT AVG(TIMESTAMPDIFF(YEAR, e.date_of_birth, CURRENT_DATE)) FROM employee e", nativeQuery = true)
    Double findAveAge();

    //display list of employees between the parameter of minAge and maxAge
    @Query(value = "SELECT * FROM employee e WHERE TIMESTAMPDIFF(YEAR , e.date_of_birth, CURRENT_DATE) BETWEEN :minAge AND :maxAge",
    countQuery= "SELECT COUNT(*) FROM employee e WHERE TIMESTAMPDIFF(YEAR , e.date_of_birth, CURRENT_DATE) BETWEEN :minAge and :maxAge",
    nativeQuery = true)
    Page<Employee> findAgeBetween(@Param("minAge") int minAge, @Param("maxAge") int maxAge, Pageable pageable);

}
