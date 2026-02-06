package com.capstone.employee_management.controller;

import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.service.EmployeeService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminPageController {

    private final EmployeeService employeeService;

    AdminPageController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public List<EmployeeResponseDto> getEmployees(Pageable pageable) {
        return employeeService.findAll();
    }

    @GetMapping("/admin/reports")
    public String reportsPage() {
        return "admin/reports";
    }


}
