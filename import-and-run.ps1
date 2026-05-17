# Script để import dữ liệu reviews và chạy Spring Boot application

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  IMPORT REVIEWS DATA & RUN PROJECT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Thông tin database
$dbHost = "localhost"
$dbPort = "3306"
$dbName = "tmdt"
$dbUser = "root"
$dbPassword = "12345"
$sqlFile = "src\main\resources\data\seed_reviews.sql"

# Kiểm tra file SQL có tồn tại không
if (-Not (Test-Path $sqlFile)) {
    Write-Host "ERROR: File SQL không tồn tại: $sqlFile" -ForegroundColor Red
    exit 1
}

Write-Host "[1/3] Đang import dữ liệu reviews vào database..." -ForegroundColor Yellow

# Tìm MySQL executable
$mysqlPaths = @(
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe",
    "C:\xampp\mysql\bin\mysql.exe",
    "C:\wamp64\bin\mysql\mysql8.0.27\bin\mysql.exe"
)

$mysqlExe = $null
foreach ($path in $mysqlPaths) {
    if (Test-Path $path) {
        $mysqlExe = $path
        break
    }
}

if ($null -eq $mysqlExe) {
    # Thử tìm mysql trong PATH
    $mysqlExe = (Get-Command mysql -ErrorAction SilentlyContinue).Source
}

if ($null -eq $mysqlExe) {
    Write-Host "WARNING: Không tìm thấy MySQL client!" -ForegroundColor Yellow
    Write-Host "Vui lòng import file SQL thủ công:" -ForegroundColor Yellow
    Write-Host "  File: $sqlFile" -ForegroundColor White
    Write-Host "  Database: $dbName" -ForegroundColor White
    Write-Host ""
    Write-Host "Bạn có thể sử dụng MySQL Workbench để import file này." -ForegroundColor Yellow
    Write-Host ""
    $continue = Read-Host "Bạn đã import dữ liệu chưa? (y/n)"
    if ($continue -ne "y") {
        Write-Host "Thoát chương trình." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "Tìm thấy MySQL tại: $mysqlExe" -ForegroundColor Green
    
    # Import SQL file
    try {
        $sqlContent = Get-Content $sqlFile -Raw -Encoding UTF8
        $sqlContent | & $mysqlExe -h $dbHost -P $dbPort -u $dbUser -p$dbPassword $dbName 2>&1 | Out-Null
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Import dữ liệu thành công!" -ForegroundColor Green
        } else {
            Write-Host "✗ Có lỗi khi import dữ liệu" -ForegroundColor Red
            Write-Host "Vui lòng kiểm tra lại thông tin database" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "✗ Lỗi: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "[2/3] Đang kiểm tra dữ liệu..." -ForegroundColor Yellow

if ($null -ne $mysqlExe) {
    try {
        $query = "SELECT COUNT(*) as total FROM reviews;"
        $result = & $mysqlExe -h $dbHost -P $dbPort -u $dbUser -p$dbPassword $dbName -e $query -s -N 2>&1
        
        if ($result -match '^\d+$') {
            Write-Host "✓ Tổng số reviews trong database: $result" -ForegroundColor Green
        }
    } catch {
        Write-Host "Không thể kiểm tra dữ liệu" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "[3/3] Đang khởi động Spring Boot application..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Nhấn Ctrl+C để dừng server" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Chạy Spring Boot application
if (Test-Path "mvnw.cmd") {
    .\mvnw.cmd spring-boot:run
} elseif (Test-Path "mvnw") {
    .\mvnw spring-boot:run
} else {
    mvn spring-boot:run
}
