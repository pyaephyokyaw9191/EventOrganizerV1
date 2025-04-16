# HTTP Tests for Event Management System

This directory contains HTTP test files for testing the Event Management System services.

## Prerequisites

- IntelliJ IDEA with the HTTP Client plugin installed
- All services running (Auth, User, Event, Registration)

## Running the Services

You can use the provided PowerShell script to start all services:

```powershell
.\start-services.ps1
```

Or start each service manually in the following order:

1. Auth Service (port 8080)
2. User Service (port 8082)
3. Event Service (port 8083)
4. Registration Service (port 8084)

## Available Test Files

- `api-flow.http`: Tests the complete flow across all services
- `registration-service.http`: Tests the Registration Service specifically

## Running Tests in IntelliJ

1. Open the HTTP test file in IntelliJ
2. Make sure all services are running
3. Click the green "Run" button next to each request to execute it individually
4. Or use the "Run All Requests" option to execute all requests in the file

## Test Flow

The tests follow this general flow:

1. Authenticate to get a JWT token
2. Create a user profile
3. Create an event
4. Register for the event
5. Retrieve registration details
6. Cancel the registration

## Troubleshooting

- If you get a 403 Forbidden error, check that your JWT token is valid
- If you get a 404 Not Found error, check that the service is running and the endpoint path is correct
- If you get a 500 Internal Server Error, check the service logs for details 