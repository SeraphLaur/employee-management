package com.capstone.employee_management.controller;

import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.service.EmployeeManagementService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminPageController {

    private final EmployeeManagementService employeeManagementService;

    AdminPageController(EmployeeManagementService employeeManagementService) {
        this.employeeManagementService = employeeManagementService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public List<EmployeeResponseDto> getEmployees(Pageable pageable) {
        return employeeManagementService.findAll();
    }

    @GetMapping("/admin/reports")
    public String reportsPage() {
        return "admin/reports";
    }


}
