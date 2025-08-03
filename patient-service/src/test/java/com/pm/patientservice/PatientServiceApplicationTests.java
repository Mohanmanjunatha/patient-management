package com.pm.patientservice;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exceptions.EmailAlreadyExistException;
import com.pm.patientservice.exceptions.PatientNotFoundException;
import com.pm.patientservice.repository.PatientRepository;
import com.pm.patientservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PatientServiceApplicationTests {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/patients";
    }

    @Test
    void contextLoads() {
        assertNotNull(patientService);
        assertNotNull(patientRepository);
    }

    @Test
    void testCreatePatient() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("Test Patient");
        request.setEmail("test@example.com");
        request.setAddress("123 Test St");
        request.setDateOfBirth("1990-01-01");
        request.setRegisteredDate("2024-01-01");

        PatientResponseDTO response = patientService.createPatient(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Test Patient", response.getName());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testCreatePatientWithDuplicateEmail() {
        PatientRequestDTO request1 = new PatientRequestDTO();
        request1.setName("Test Patient 1");
        request1.setEmail("duplicate@example.com");
        request1.setAddress("123 Test St");
        request1.setDateOfBirth("1990-01-01");
        request1.setRegisteredDate("2024-01-01");

        PatientRequestDTO request2 = new PatientRequestDTO();
        request2.setName("Test Patient 2");
        request2.setEmail("duplicate@example.com");
        request2.setAddress("456 Test St");
        request2.setDateOfBirth("1991-01-01");
        request2.setRegisteredDate("2024-01-02");

        patientService.createPatient(request1);
        assertThrows(EmailAlreadyExistException.class, () -> patientService.createPatient(request2));
    }

    @Test
    void testGetPatients() {
        for (int i = 0; i < 20; i++) {
            PatientRequestDTO request = new PatientRequestDTO();
            request.setName("Patient " + i);
            request.setEmail("patient" + i + "@example.com");
            request.setAddress("123 Test St");
            request.setDateOfBirth("1990-01-01");
            request.setRegisteredDate("2024-01-01");
            patientService.createPatient(request);
        }

        List<PatientResponseDTO> patients = patientService.getPatients();

        assertNotNull(patients);
        assertTrue(patients.size() >= 15);
    }

    @Test
    void testGetPatientsWithPagination() {
        for (int i = 0; i < 20; i++) {
            PatientRequestDTO request = new PatientRequestDTO();
            request.setName("Paginated " + i);
            request.setEmail("paginate" + i + "@example.com");
            request.setAddress("123 Test St");
            request.setDateOfBirth("1990-01-01");
            request.setRegisteredDate("2024-01-01");
            patientService.createPatient(request);
        }

        Page<PatientResponseDTO> patients = patientService.getPatients(
                org.springframework.data.domain.PageRequest.of(0, 5));

        assertNotNull(patients);
        assertEquals(5, patients.getContent().size());
        assertTrue(patients.getTotalElements() >= 15);
    }

    @Test
    void testGetPatientById() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("Test Patient");
        request.setEmail("testbyid@example.com");
        request.setAddress("123 Test St");
        request.setDateOfBirth("1990-01-01");
        request.setRegisteredDate("2024-01-01");

        PatientResponseDTO created = patientService.createPatient(request);
        UUID patientId = UUID.fromString(created.getId());

        PatientResponseDTO found = patientService.getPatientById(patientId);

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Test Patient", found.getName());
    }

    @Test
    void testGetPatientByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(nonExistentId));
    }

    @Test
    void testUpdatePatient() {
        PatientRequestDTO createRequest = new PatientRequestDTO();
        createRequest.setName("Original Name");
        createRequest.setEmail("original@example.com");
        createRequest.setAddress("123 Original St");
        createRequest.setDateOfBirth("1990-01-01");
        createRequest.setRegisteredDate("2024-01-01");

        PatientResponseDTO created = patientService.createPatient(createRequest);
        UUID patientId = UUID.fromString(created.getId());

        PatientRequestDTO updateRequest = new PatientRequestDTO();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setAddress("456 Updated St");
        updateRequest.setDateOfBirth("1991-01-01");

        PatientResponseDTO updated = patientService.updatePatient(patientId, updateRequest);

        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void testUpdatePatientNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("Test");
        request.setEmail("test@example.com");
        request.setAddress("123 Test St");
        request.setDateOfBirth("1990-01-01");

        assertThrows(PatientNotFoundException.class, () -> patientService.updatePatient(nonExistentId, request));
    }

    @Test
    void testDeletePatient() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("Test Patient");
        request.setEmail("testdelete@example.com");
        request.setAddress("123 Test St");
        request.setDateOfBirth("1990-01-01");
        request.setRegisteredDate("2024-01-01");

        PatientResponseDTO created = patientService.createPatient(request);
        UUID patientId = UUID.fromString(created.getId());

        patientService.deletePatient(patientId);

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(patientId));
    }

    @Test
    void testSearchPatientsByName() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("Search Test Patient");
        request.setEmail("searchtest@example.com");
        request.setAddress("123 Search St");
        request.setDateOfBirth("1990-01-01");
        request.setRegisteredDate("2024-01-01");

        patientService.createPatient(request);

        List<PatientResponseDTO> results = patientService.searchPatients("Search Test", null, null);

        assertNotNull(results);
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().anyMatch(p -> p.getName().contains("Search Test")));
    }

    @Test
    void testSearchPatientsByEmail() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("Email Search Test");
        request.setEmail("emailsearch@example.com");
        request.setAddress("123 Email St");
        request.setDateOfBirth("1990-01-01");
        request.setRegisteredDate("2024-01-01");

        patientService.createPatient(request);

        List<PatientResponseDTO> results = patientService.searchPatients(null, "emailsearch", null);

        assertNotNull(results);
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().anyMatch(p -> p.getEmail().contains("emailsearch")));
    }

    @Test
    void testHealthCheckEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/actuator/health", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("UP"));
    }

    @Test
    void testGetPatientsEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreatePatientEndpoint() {
        String requestBody = """
            {
                "name": "API Test Patient",
                "email": "apitest@example.com",
                "address": "123 API St",
                "dateOfBirth": "1990-01-01",
                "registeredDate": "2024-01-01"
            }
            """;

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("API Test Patient"));
    }
}
