# Start services in the correct order
Write-Host "Starting Event Management System Services..." -ForegroundColor Green

# Start Auth Service
Write-Host "Starting Auth Service..." -ForegroundColor Cyan
Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -WorkingDirectory ".\auth-service" -NoNewWindow

# Wait for Auth Service to start
Write-Host "Waiting for Auth Service to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Start User Service
Write-Host "Starting User Service..." -ForegroundColor Cyan
Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -WorkingDirectory ".\user-service" -NoNewWindow

# Wait for User Service to start
Write-Host "Waiting for User Service to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Start Event Service
Write-Host "Starting Event Service..." -ForegroundColor Cyan
Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -WorkingDirectory ".\event-service" -NoNewWindow

# Wait for Event Service to start
Write-Host "Waiting for Event Service to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Start Registration Service
Write-Host "Starting Registration Service..." -ForegroundColor Cyan
Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -WorkingDirectory ".\registration-service" -NoNewWindow

Write-Host "All services started!" -ForegroundColor Green
Write-Host "You can now run the HTTP tests in IntelliJ." -ForegroundColor Green 