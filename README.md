# Patient Management System

A Spring Boot REST API for managing patient records.

## Features

- CRUD operations for patients
- RESTful API endpoints
- H2 in-memory database
- Swagger documentation

## Technology Stack

- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- H2 Database
- Maven

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd patient-management
```

2. **Start the application**
```bash
cd patient-service
./mvnw spring-boot:run
```

3. **Access the application**
- **API Base URL**: `http://localhost:4000`
- **Swagger UI**: `http://localhost:4000/swagger-ui.html`
- **H2 Console**: `http://localhost:4000/h2-console`

## API Endpoints

### Patient Management
- `GET /patients` - Get all patients
- `GET /patients/{id}` - Get patient by ID
- `POST /patients` - Create new patient
- `PUT /patients/{id}` - Update patient
- `DELETE /patients/{id}` - Delete patient

## Database Schema

### Patient Entity
- `id` (UUID) - Primary key
- `name` (String) - Patient name
- `email` (String) - Unique email address
- `address` (String) - Patient address
- `dateOfBirth` (LocalDate) - Date of birth
- `registeredDate` (LocalDate) - Registration date

## Configuration

Key configuration properties in `application.properties`:

```properties
server.port=4000
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
```

## Testing

Run the test suite:
```bash
./mvnw test
```

## License

This project is licensed under the MIT License. 