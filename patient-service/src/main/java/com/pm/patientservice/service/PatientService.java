package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exceptions.EmailAlreadyExistException;
import com.pm.patientservice.exceptions.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Page<PatientResponseDTO> getPatients(Pageable pageable) {
        log.info("Fetching patients with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Patient> patients = patientRepository.findAll(pageable);
        log.info("Found {} patients", patients.getTotalElements());
        return patients.map(PatientMapper::toDTO);
    }

    public List<PatientResponseDTO> getPatients() {
        log.info("Fetching all patients");
        List<Patient> patients = patientRepository.findAll();
        log.info("Found {} patients", patients.size());
        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO getPatientById(UUID id) {
        log.info("Fetching patient with ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        log.info("Found patient: {}", patient.getName());
        return PatientMapper.toDTO(patient);
    }

    public List<PatientResponseDTO> searchPatients(String name, String email, String address) {
        log.info("Searching patients with criteria: name={}, email={}, address={}", name, email, address);
        List<Patient> patients;
        
        if (name != null && !name.trim().isEmpty()) {
            patients = patientRepository.findByNameContainingIgnoreCase(name.trim());
        } else if (email != null && !email.trim().isEmpty()) {
            patients = patientRepository.findByEmailContainingIgnoreCase(email.trim());
        } else if (address != null && !address.trim().isEmpty()) {
            patients = patientRepository.findByAddressContainingIgnoreCase(address.trim());
        } else {
            patients = patientRepository.findAll();
        }
        
        log.info("Found {} patients matching search criteria", patients.size());
        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        log.info("Creating new patient with email: {}", patientRequestDTO.getEmail());
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            log.warn("Attempt to create patient with existing email: {}", patientRequestDTO.getEmail());
            throw new EmailAlreadyExistException("a patient with this email" + "already Exist" +
                    patientRequestDTO.getEmail());
        }

        // patient service
        // we get as DTO object to serviuce
        // it should be convereted to model entity before getting data  into database

        Patient newpatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        log.info("Successfully created patient with ID: {}", newpatient.getId());
        return PatientMapper.toDTO(newpatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        log.info("Updating patient with ID: {}", id);
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));


        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            log.warn("Attempt to update patient with existing email: {}", patientRequestDTO.getEmail());
            throw new EmailAlreadyExistException("a patient with this email" + "already Exist" +
                    patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedpatient = patientRepository.save(patient);
        log.info("Successfully updated patient with ID: {}", updatedpatient.getId());
        return PatientMapper.toDTO(updatedpatient);


    }

    public void deletePatient(UUID id) {
        log.info("Deleting patient with ID: {}", id);
        if (!patientRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent patient with ID: {}", id);
            throw new PatientNotFoundException("Patient not found with ID: " + id);
        }
        patientRepository.deleteById(id);
        log.info("Successfully deleted patient with ID: {}", id);
    }
}
