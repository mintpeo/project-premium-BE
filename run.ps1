# Script chạy Spring Boot với JDK 21
# Tự động tìm JDK 21 đã cài trên máy

$jdk21Paths = @(
    "C:\Program Files\Eclipse Adoptium\jdk-21*",
    "C:\Program Files\Microsoft\jdk-21*",
    "C:\Program Files\Java\jdk-21*",
    "C:\Program Files\OpenJDK\jdk-21*"
)

$javaHome = $null
foreach ($pattern in $jdk21Paths) {
    $found = Get-Item $pattern -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $javaHome = $found.FullName
        break
    }
}

if (-not $javaHome) {
    Write-Host "Khong tim thay JDK 21. Vui long cai bang lenh:" -ForegroundColor Red
    Write-Host "   winget install EclipseAdoptium.Temurin.21.JDK" -ForegroundColor Yellow
    exit 1
}

Write-Host "Dung JDK 21 tai: $javaHome" -ForegroundColor Green
$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;" + $env:PATH

Write-Host "Khoi dong Spring Boot..." -ForegroundColor Cyan
./mvnw spring-boot:run
