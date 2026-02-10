package com.capstone.employee_management.controller;

import com.capstone.employee_management.dto.DepartmentRequestDto;
import com.capstone.employee_management.dto.DepartmentResponseDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.repository.DepartmentRepository;
import com.capstone.employee_management.repository.EmployeeRepository;
import com.capstone.employee_management.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    private final DepartmentService  departmentService;


    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public Page<DepartmentResponseDto> findAllDepartments(
            @RequestParam(defaultValue = "${pagination.default.page}") int page,
            @RequestParam(defaultValue = "${pagination.default.size}") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return departmentService.findAll(pageable);
    }

    @PostMapping
    public DepartmentResponseDto addDepartment(@RequestBody DepartmentRequestDto req) {
        return departmentService.createDepartment(req);
    }

    @PutMapping
    public DepartmentResponseDto updateDepartment(@PathVariable String name, @RequestBody DepartmentRequestDto req) {
        return departmentService.updateDepartment(name, req);
    }

    public void deleteDepartment(@PathVariable DepartmentRequestDto req) {
        departmentService.deleteDepartment(req);
    }
}
