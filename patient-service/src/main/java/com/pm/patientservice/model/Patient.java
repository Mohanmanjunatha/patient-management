package com.pm.patientservice.model;
// we need to store this in database so we need to mark it as entity
// and also we need a primary key top store them so we need to generate a key

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotNull
    private String address;
    @NotNull
    private LocalDate date_of_birth;
    @NotNull
    private LocalDate registered_date;

    @NotNull
    private String name;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return date_of_birth;
    }

    public void setDateOfBirth(LocalDate birthDate) {
        this.date_of_birth = birthDate;
    }

    public LocalDate getRegisteredDate() {
        return registered_date;
    }

    public void setRegisteredDate(LocalDate registered_date) {
        this.registered_date = registered_date;
    }

}
