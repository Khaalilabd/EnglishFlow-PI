# Fix tutor availability for tutor ID 11
$env:PGPASSWORD = "postgres"

Write-Host "Inserting availability for tutor ID 11..." -ForegroundColor Cyan

# Execute the SQL script
psql -h localhost -U postgres -d courses_db -f insert-tutor-availability.sql

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ Availability inserted successfully!" -ForegroundColor Green
} else {
    Write-Host "`n✗ Failed to insert availability" -ForegroundColor Red
}
