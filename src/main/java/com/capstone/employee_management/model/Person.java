package com.capstone.employee_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@MappedSuperclass
public abstract class Person {
    @Column
    @NotBlank
    protected String name;

    @Past
    @Column(name = "date_of_birth", nullable = false)
    protected LocalDate dateOfBirth;

    public abstract int calculateAge();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
