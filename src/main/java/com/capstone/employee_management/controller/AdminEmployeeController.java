package com.capstone.employee_management.controller;

import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.dto.StatisticsResponseDto;
import com.capstone.employee_management.model.Employee;
import com.capstone.employee_management.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
class AdminEmployeeController {
    private final EmployeeService employeeService;

    AdminEmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponseDto> getEmployees(Pageable pageable) {
        return employeeService.findAll();
    }

    @PostMapping("/add-employee")
    public EmployeeResponseDto addEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        return employeeService.createEmployee(employeeRequestDto);
    }

    @PutMapping("/edit-employee/{id}")
    public EmployeeResponseDto editEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDto employeeRequestDto) {
        return employeeService.updateEmployee(id, employeeRequestDto);
    }

    @DeleteMapping("/delete-employee/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/search")
    public List<EmployeeResponseDto> searchEmployees(@RequestParam String keyword) {
        return employeeService.searchEmployees(keyword);
    }

    @GetMapping("/statistics")
    public StatisticsResponseDto getStatistics() {
        return employeeService.getStatistics();
    }

    @GetMapping("/by-department")
    public Page<EmployeeResponseDto> getEmployeesByDepartment(
            @RequestParam String departmentName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeService.findByDepartment(departmentName, pageable);
    }

    @GetMapping("/by-age")
    public Page<EmployeeResponseDto> getEmployeesByAge(
            @RequestParam int minAge,
            @RequestParam int maxAge,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeService.findByAgeRange(minAge, maxAge, pageable);
    }

}
