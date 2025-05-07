# Clean PowerShell script to move student-related files to /archive
$files = @(
    "xmart-city-backend/src/main/java/edu/ezip/ing1/pds/business/server/XMartCityService.java",
    "xmart-city-backend/src/main/java/edu/ezip/ing1/pds/business/server/RequestOrderAnnotation.java",
    "xmart-city-backend/insert-request.json",
    "xmart-city-backend/select-request.json",
    "xmart-city-backend/select-1-request.json",
    "xmart-city-business-dto/src/main/java/edu/ezip/ing1/pds/business/dto/Student.java",
    "xmart-city-business-dto/src/main/java/edu/ezip/ing1/pds/business/dto/Students.java",
    "xmart-frontend/src/main/java/edu/ezip/ing1/pds/requests/InsertStudentsClientRequest.java",
    "xmart-frontend/src/main/java/edu/ezip/ing1/pds/requests/SelectAllStudentsClientRequest.java",
    "xmart-frontend/src/main/java/edu/ezip/ing1/pds/services/StudentService.java",
    "xmart-frontend/src/main/java/edu/ezip/ing1/pds/MainFrontEnd.java",
    "xmart-frontend/src/main/resources/students-to-be-inserted.yaml"
)

$archivePath = "archive"
if (!(Test-Path $archivePath)) {
    New-Item -ItemType Directory -Path $archivePath | Out-Null
}

foreach ($file in $files) {
    if (Test-Path $file) {
        $filename = Split-Path $file -Leaf
        Move-Item -Path $file -Destination "$archivePath\$filename"
        Write-Host "Moved: $file"
    }
    else {
        Write-Host "Not found: $file"
    }
}
