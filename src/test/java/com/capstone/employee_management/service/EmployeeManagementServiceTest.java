package com.capstone.employee_management.service;

import com.capstone.employee_management.dto.EmployeeMapper;
import com.capstone.employee_management.dto.EmployeeRequestDto;
import com.capstone.employee_management.dto.EmployeeResponseDto;
import com.capstone.employee_management.dto.StatisticsResponseDto;
import com.capstone.employee_management.model.Department;
import com.capstone.employee_management.model.Employee;
import com.capstone.employee_management.repository.DepartmentRepository;
import com.capstone.employee_management.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeManagementServiceTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock DepartmentRepository departmentRepository;
    @Mock EmployeeMapper employeeMapper;

    // Minimal concrete class to instantiate the abstract service and satisfy EmployeeService
    static class TestEmployeeService extends EmployeeManagementService {
        public TestEmployeeService(DepartmentRepository departmentRepository,
                                   EmployeeRepository employeeRepository,
                                   EmployeeMapper employeeMapper) {
            super(departmentRepository, employeeRepository, employeeMapper);
        }

        // since employeeservices have other methods, other methods aren't used in this tests
        @Override
        public Page<EmployeeResponseDto> findByDepartment(String departmentName, Pageable pageable) {
            throw new UnsupportedOperationException("Not used in this test");
        }

        @Override
        public StatisticsResponseDto getStatistics() {
            throw new UnsupportedOperationException("Not used in this test");
        }

        @Override
        public Page<EmployeeResponseDto> findByAge(Integer minAge, Integer maxAge, Pageable pageable) {
            throw new UnsupportedOperationException("Not used in this test");
        }

        @Override
        public Page<EmployeeResponseDto> findByAge(Integer age, Pageable pageable) {
            throw new UnsupportedOperationException("Not used in this test");
        }
    }

    EmployeeManagementService service;

    @BeforeEach
    void setUp() {
        service = new TestEmployeeService(departmentRepository, employeeRepository, employeeMapper);
    }

    // helpers that matches my created DTOs

    private EmployeeRequestDto req(String empCode, String name, LocalDate dob, String deptName, BigDecimal salary) {
        return new EmployeeRequestDto(empCode, name, dob, deptName, salary);
    }

    private EmployeeResponseDto resp(Long id,
                                     String empCode,
                                     String name,
                                     LocalDate birthday,
                                     String deptName,
                                     BigDecimal salary,
                                     Integer age) {
        return new EmployeeResponseDto(id, empCode, name, birthday, deptName, salary, age);
    }

    private Department dept(Long id, String name) {
        Department d = new Department();
        d.setId(id);
        d.setName(name);
        return d;
    }


    private Employee emp(Long id, String empCode, String name, LocalDate birthday, Department d, BigDecimal salary) {
        Employee e = new Employee();
        e.setId(id);
        e.setEmployeeId(empCode);
        e.setName(name);
        e.setDateOfBirth(birthday);
        e.setDepartment(d);
        e.setSalary(salary);
        return e;
    }

    // basic tests for the employee services

    @Test
    void findAll_returnsMappedResponses() {
        Department d = dept(1L, "IT");
        Employee e1 = emp(10L, "E001", "Alice", LocalDate.of(1990, 1, 1), d, new BigDecimal("1000"));
        Employee e2 = emp(11L, "E002", "Bob",   LocalDate.of(1991, 2, 2), d, new BigDecimal("2000"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(e1, e2), pageable, 2);

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.toResponse(e1))
                .thenReturn(resp(10L, "E001", "Alice", e1.getDateOfBirth(), "IT", e1.getSalary(), 34));
        when(employeeMapper.toResponse(e2))
                .thenReturn(resp(11L, "E002", "Bob",   e2.getDateOfBirth(), "IT", e2.getSalary(), 33));

        Page<EmployeeResponseDto> results = service.findAll(pageable);

        assertEquals(2, results.getContent().size());
        assertEquals(2, results.getTotalElements());
        assertEquals("E001", results.getContent().get(0).employeeCode());
        assertEquals("E002", results.getContent().get(1).employeeCode());
        verify(employeeRepository).findAll(pageable);
        verify(employeeMapper, times(2)).toResponse(any(Employee.class));
    }

    @Test
    void searchEmployees_returnsMappedResponses() {
        Department d = dept(2L, "HR");
        Employee e = emp(20L, "E010", "Carla", LocalDate.of(1988, 5, 5), d, new BigDecimal("3000"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(e), pageable, 1);

        when(employeeRepository.searchEmployees("car", pageable)).thenReturn(employeePage);
        when(employeeMapper.toResponse(e))
                .thenReturn(resp(20L, "E010", "Carla", e.getDateOfBirth(), "HR", e.getSalary(), 37));

        Page<EmployeeResponseDto> results = service.searchEmployees("car", pageable);

        assertEquals(1, results.getContent().size());
        assertEquals(1, results.getTotalElements());
        assertEquals("E010", results.getContent().get(0).employeeCode());
        verify(employeeRepository).searchEmployees("car", pageable);
        verify(employeeMapper).toResponse(e);
    }

    @Test
    void createEmployee_departmentExists_savesWithExistingDepartment() {
        var request = req("E777", "Diana", LocalDate.of(1990, 1, 1), "Finance", new BigDecimal("100000"));
        var existingDept = dept(7L, "Finance");
        var toSave = new Employee();
        var saved = emp(77L, "E777", "Diana", request.dateOfBirth(), existingDept, request.salary());
        var mapped = resp(77L, "E777", "Diana", request.dateOfBirth(), "Finance", request.salary(), 36);

        when(employeeRepository.existsByEmployeeIdIgnoreCase("E777")).thenReturn(false);
        when(departmentRepository.existsByNameIgnoreCase("Finance")).thenReturn(true);
        when(departmentRepository.findByNameIgnoreCase("Finance")).thenReturn(Optional.of(existingDept));
        when(employeeMapper.toEntity(request, existingDept)).thenReturn(toSave);
        when(employeeRepository.save(toSave)).thenReturn(saved);
        when(employeeMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.createEmployee(request);

        assertEquals("E777", result.employeeCode());
        assertEquals("Finance", result.department());
        assertEquals(request.dateOfBirth(), result.dateOfBirth());
        verify(employeeRepository).existsByEmployeeIdIgnoreCase("E777");
        verify(departmentRepository).existsByNameIgnoreCase("Finance");
        verify(departmentRepository).findByNameIgnoreCase("Finance");
        verify(employeeMapper).toEntity(request, existingDept);
        verify(employeeRepository).save(toSave);
        verify(employeeMapper).toResponse(saved);
    }

    @Test
    void createEmployee_departmentDoesNotExist_createsDepartmentThenSaves() {
        var request = req("E888", "Evan", LocalDate.of(1995, 6, 15), "NewDept", new BigDecimal("90000"));
        var toSave = new Employee();
        var savedEmp = emp(88L, "E888", "Evan", request.dateOfBirth(), dept(99L, "NewDept"), request.salary());
        var mapped = resp(88L, "E888", "Evan", request.dateOfBirth(), "NewDept", request.salary(), 30);

        when(employeeRepository.existsByEmployeeIdIgnoreCase("E888")).thenReturn(false);
        when(departmentRepository.existsByNameIgnoreCase("NewDept")).thenReturn(false);

        // Simulate persistence assigning ID to new department
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> {
            Department nd = inv.getArgument(0);
            nd.setId(99L);
            return nd;
        });

        when(employeeMapper.toEntity(eq(request), any(Department.class))).thenReturn(toSave);
        when(employeeRepository.save(toSave)).thenReturn(savedEmp);
        when(employeeMapper.toResponse(savedEmp)).thenReturn(mapped);

        var result = service.createEmployee(request);

        assertEquals("E888", result.employeeCode());
        assertEquals("NewDept", result.department());
        verify(departmentRepository).save(any(Department.class));
        verify(employeeRepository).save(toSave);
    }

    @Test
    void createEmployee_employeeIdAlreadyExists_throws() {
        var request = req("E999", "Frank", LocalDate.of(1992, 3, 3), "Ops", new BigDecimal("50000"));
        when(employeeRepository.existsByEmployeeIdIgnoreCase("E999")).thenReturn(true);

        var ex = assertThrows(IllegalArgumentException.class, () -> service.createEmployee(request));

        assertTrue(ex.getMessage().contains("E999"));
        verify(employeeRepository).existsByEmployeeIdIgnoreCase("E999");
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(departmentRepository, employeeMapper);
    }

    @Test
    void updateEmployee_happyPath_updatesAndSaves_withExistingDept() {
        var existingDept = dept(1L, "IT");
        var existing = emp(100L, "E100", "Grace", LocalDate.of(1988, 12, 12), existingDept, new BigDecimal("120000"));
        var request = req("E100", "Grace V2", LocalDate.of(1988, 12, 12), "IT", new BigDecimal("120000"));
        var mapped = resp(100L, "E100", "Grace V2", request.dateOfBirth(), "IT", request.salary(), 37);

        when(employeeRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmployeeIdIgnoreCase("E100")).thenReturn(true);
        when(departmentRepository.existsByNameIgnoreCase("IT")).thenReturn(true);
        when(departmentRepository.findByNameIgnoreCase("IT")).thenReturn(Optional.of(existingDept));
        // updateEntity mutates 'existing'
        doAnswer(inv -> null).when(employeeMapper).updateEntity(existing, request, existingDept);
        when(employeeRepository.save(existing)).thenReturn(existing);
        when(employeeMapper.toResponse(existing)).thenReturn(mapped);

        var result = service.updateEmployee(100L, request);

        assertEquals(100L, result.id());
        assertEquals("Grace V2", result.name());
        verify(employeeRepository).findById(100L);
        verify(employeeRepository).existsByEmployeeIdIgnoreCase("E100");
        verify(departmentRepository).existsByNameIgnoreCase("IT");
        verify(employeeMapper).updateEntity(existing, request, existingDept);
        verify(employeeRepository).save(existing);
        verify(employeeMapper).toResponse(existing);
    }

    @Test
    void updateEmployee_changingEmployeeId_newIdAlreadyExists_throws() {
        var existingDept = dept(1L, "IT");
        var existing = emp(100L, "E100", "Grace", LocalDate.of(1988, 12, 12), existingDept, new BigDecimal("120000"));
        var request = req("E999", "Grace V2", LocalDate.of(1988, 12, 12), "IT", new BigDecimal("120000"));

        when(employeeRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmployeeIdIgnoreCase("E999")).thenReturn(true);

        var ex = assertThrows(IllegalArgumentException.class, () -> service.updateEmployee(100L, request));

        assertTrue(ex.getMessage().toLowerCase().contains("already exists") ||
                ex.getMessage().toLowerCase().contains("previous id"));
        verify(employeeRepository).findById(100L);
        verify(employeeRepository).existsByEmployeeIdIgnoreCase("E999");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_employeeNotFound_throws() {
        var request = req("E100", "Grace", LocalDate.of(1988, 12, 12), "IT", new BigDecimal("120000"));

        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        var ex = assertThrows(EntityNotFoundException.class, () -> service.updateEmployee(999L, request));

        assertTrue(ex.getMessage().contains("999"));
        verify(employeeRepository).findById(999L);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void deleteEmployee_existing_deletes() {
        when(employeeRepository.existsById(123L)).thenReturn(true);

        service.deleteEmployee(123L);

        verify(employeeRepository).existsById(123L);
        verify(employeeRepository).deleteById(123L);
    }

    @Test
    void deleteEmployee_notFound_throws() {
        when(employeeRepository.existsById(124L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.deleteEmployee(124L));

        verify(employeeRepository).existsById(124L);
        verify(employeeRepository, never()).deleteById(anyLong());
    }
}