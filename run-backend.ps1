Write-Host "Starting Spring Boot Backend..." -ForegroundColor Green
Write-Host "Backend will run at: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Press Ctrl+C to stop" -ForegroundColor Yellow
Write-Host ""

if (Test-Path "mvnw.cmd") {
    .\mvnw.cmd spring-boot:run
} else {
    mvn spring-boot:run
}
