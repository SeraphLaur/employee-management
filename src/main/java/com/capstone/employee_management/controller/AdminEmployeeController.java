package com.capstone.employee_management.controller;

import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.dto.StatisticsResponseDto;
import com.capstone.employee_management.service.EmployeeService;
import com.capstone.employee_management.service.EmployeeTransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.admin.path}")
class AdminEmployeeController {
    private final EmployeeService employeeService;

    AdminEmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public Page<EmployeeResponseDto> getEmployees(
            @RequestParam(defaultValue = "${pagination.default.page}") int page,
            @RequestParam(defaultValue = "${pagination.default.size}") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeService.findAll(pageable);
    }

    @PostMapping("${api.admin.employee.add}")
    public EmployeeResponseDto addEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        return employeeService.createEmployee(employeeRequestDto);
    }

    @PutMapping("${api.admin.employee.edit}")
    public EmployeeResponseDto editEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDto employeeRequestDto) {
        return employeeService.updateEmployee(id, employeeRequestDto);
    }

    @DeleteMapping("${api.admin.employee.delete}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("${api.admin.search}")
    public Page<EmployeeResponseDto> searchEmployees(@RequestParam String keyword,
                                                     @RequestParam(defaultValue = "${pagination.default.page}") int page,
                                                     @RequestParam(defaultValue = "${pagination.default.size}") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeService.searchEmployees(keyword, pageable);
    }

    @GetMapping("${api.admin.statistics}")
    public StatisticsResponseDto getStatistics() {
        return employeeService.getStatistics();
    }

    @GetMapping("${api.admin.sort.department}")
    public Page<EmployeeResponseDto> getEmployeesByDepartment(
            @RequestParam String departmentName,
            @RequestParam(defaultValue = "${pagination.default.page}") int page,
            @RequestParam(defaultValue = "${pagination.default.size}") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeService.findByDepartment(departmentName, pageable);
    }

    @GetMapping("${api.admin.sort.age}")
    public Page<EmployeeResponseDto> getEmployeesByAge(
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(defaultValue = "${pagination.default.page}") int page,
            @RequestParam(defaultValue = "${pagination.default.size}") int size) {
        Pageable pageable = PageRequest.of(page, size);

        if(age != null) {
            if(age < 0) {
                throw new IllegalArgumentException("Age cannot be negative");
            }
            return employeeService.findByAge(age, pageable);
        }
        else if(minAge != null || maxAge != null) {
            return employeeService.findByAge(minAge, maxAge, pageable);
        }

        throw new IllegalArgumentException("Please provide an age.");
    }

}
