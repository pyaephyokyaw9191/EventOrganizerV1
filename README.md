# Event Management System MVP

A microservices-based event management system built with Spring Boot.

## Services

1. **Auth Service** (Port: 8080)
   - User authentication and authorization
   - JWT token management

2. **User Service** (Port: 8081)
   - User profile management
   - CRUD operations for user data

3. **Event Service** (Port: 8082)
   - Event creation and management
   - Event search and filtering

4. **Registration Service** (Port: 8083)
   - Event registration management
   - Ticket handling

5. **Payment Service** (Port: 8084)
   - Payment processing
   - Receipt generation

## Technology Stack

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database
- JWT Authentication
- Maven
- Lombok

## Getting Started

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean install
   ```

3. Start the services using the provided PowerShell script:
   ```powershell
   .\start-services.ps1
   ```

   Or start each service individually in the following order:
   ```bash
   cd auth-service
   mvn spring-boot:run
   
   cd ../user-service
   mvn spring-boot:run
   
   cd ../event-service
   mvn spring-boot:run
   
   cd ../registration-service
   mvn spring-boot:run
   
   cd ../payment-service
   mvn spring-boot:run
   ```

## Testing with HTTP Tests

The project includes HTTP test files for testing the services using IntelliJ IDEA's HTTP Client plugin.

### Prerequisites
- IntelliJ IDEA with the HTTP Client plugin installed
- All services running

### Available Test Files
Located in the `http-tests` directory:
- `full-application-flow.http`: Tests the complete flow across all services
- `registration-service.http`: Tests the Registration Service specifically

### Running Tests in IntelliJ
1. Open any `.http` file in IntelliJ
2. Ensure all services are running
3. Click the green "Run" button next to each request to execute it individually
4. Or use the "Run All Requests" option to execute all requests in the file

### Test Flow
The tests follow this general flow:
1. Authenticate to get a JWT token
2. Create a user profile
3. Create an event
4. Register for the event
5. Retrieve registration details
6. Cancel the registration

## Database Access

Each service has its own H2 database console accessible at:
- http://localhost:<service-port>/h2-console

Default credentials:
- Username: sa
- Password: (empty)

## API Documentation

Each service exposes its own REST endpoints. Detailed API documentation will be available at:
- http://localhost:<service-port>/swagger-ui.html (when implemented)

## Troubleshooting

- If you get a 403 Forbidden error, check that your JWT token is valid
- If you get a 404 Not Found error, check that the service is running and the endpoint path is correct
- If you get a 500 Internal Server Error, check the service logs for details 