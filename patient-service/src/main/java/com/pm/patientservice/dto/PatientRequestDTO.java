package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PatientRequestDTO {
    @NotBlank
    @Size(max = 100, message = "name cannot exceed 100 characters")
    private String name;
    @NotBlank(message = "Email is Required ")

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank
    private String address;

    @NotBlank
    private String date_of_birth;

    @NotBlank(groups = CreatePatientValidationGroup.class,message = "Registered date is required")
    private String registered_date;

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

    public String getDateOfBirth() {
        return date_of_birth;
    }

    public void setDateOfBirth(
            String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getRegisteredDate() {
       return registered_date;
    }

    public void setRegisteredDate(String registered_date) {
        this.registered_date = registered_date;
    }

}
