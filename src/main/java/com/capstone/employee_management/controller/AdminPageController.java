package com.capstone.employee_management.controller;

import com.capstone.employee_management.dto.DepartmentResponseDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.service.DepartmentService;
import com.capstone.employee_management.service.EmployeeManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminPageController {

    private final EmployeeManagementService employeeManagementService;
    private final DepartmentService departmentService;

    AdminPageController(EmployeeManagementService employeeManagementService, DepartmentService departmentService) {
        this.employeeManagementService = employeeManagementService;
        this.departmentService = departmentService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public String dashboardPage() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/department")
    public String departmentPage() {
        return "admin/department";
    }

    @GetMapping("/admin/reports")
    public String reportsPage() {
        return "admin/reports";
    }


}
