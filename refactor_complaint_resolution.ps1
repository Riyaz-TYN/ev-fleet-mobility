# PowerShell script to refactor Complaint Resolution module
# This script handles the major refactoring to align with EV Fleet Mobility standards

$sourceDir = ".\src\main\java\com\evfleetmobility\Complaint_Resolution"
$targetBaseDir = ".\src\main\java\com\evfleetmobility\complaintresolution"

# Package mappings
$packageMappings = @{
    'com.complaint_resolution.complaint.controller' = 'com.evfleetmobility.complaintresolution.complaintservices.complaint.controller'
    'com.complaint_resolution.complaint.dto' = 'com.evfleetmobility.complaintresolution.complaintservices.complaint.dto'
    'com.complaint_resolution.complaint.entity' = 'com.evfleetmobility.complaintresolution.complaintservices.complaint.entity'
    'com.complaint_resolution.complaint.repository' = 'com.evfleetmobility.complaintresolution.complaintservices.complaint.repository'
    'com.complaint_resolution.complaint.service' = 'com.evfleetmobility.complaintresolution.complaintservices.complaint.service'
    'com.complaint_resolution.complaint.service.impl' = 'com.evfleetmobility.complaintresolution.complaintservices.complaint.service.impl'
    'com.complaint_resolution.vendor.controller' = 'com.evfleetmobility.complaintresolution.complaintservices.vendor.controller'
    'com.complaint_resolution.vendor.dto' = 'com.evfleetmobility.complaintresolution.complaintservices.vendor.dto'
    'com.complaint_resolution.vendor.entity' = 'com.evfleetmobility.complaintresolution.complaintservices.vendor.entity'
    'com.complaint_resolution.vendor.repository' = 'com.evfleetmobility.complaintresolution.complaintservices.vendor.repository'
    'com.complaint_resolution.vendor.service' = 'com.evfleetmobility.complaintresolution.complaintservices.vendor.service'
    'com.complaint_resolution.vendor.service.impl' = 'com.evfleetmobility.complaintresolution.complaintservices.vendor.service.impl'
    'com.complaint_resolution.auditlog.controller' = 'com.evfleetmobility.complaintresolution.complaintservices.audit.controller'
    'com.complaint_resolution.auditlog.entity' = 'com.evfleetmobility.complaintresolution.complaintservices.audit.entity'
    'com.complaint_resolution.auditlog.repository' = 'com.evfleetmobility.complaintresolution.complaintservices.audit.repository'
    'com.complaint_resolution.auditlog.service' = 'com.evfleetmobility.complaintresolution.complaintservices.audit.service'
    'com.complaint_resolution.auditlog.service.impl' = 'com.evfleetmobility.complaintresolution.complaintservices.audit.service.impl'
    'com.complaint_resolution.escalation.service' = 'com.evfleetmobility.complaintresolution.complaintservices.escalation.service'
    'com.complaint_resolution.escalation.service.impl' = 'com.evfleetmobility.complaintresolution.complaintservices.escalation.service.impl'
    'com.complaint_resolution.notification.service' = 'com.evfleetmobility.complaintresolution.complaintservices.notification.service'
    'com.complaint_resolution.notification.service.impl' = 'com.evfleetmobility.complaintresolution.complaintservices.notification.service.impl'
    'com.complaint_resolution.manager.controller' = 'com.evfleetmobility.complaintresolution.managerservices.controller'
    'com.complaint_resolution.manager.dto' = 'com.evfleetmobility.complaintresolution.managerservices.dto'
    'com.complaint_resolution.manager.service' = 'com.evfleetmobility.complaintresolution.managerservices.service'
    'com.complaint_resolution.manager.service.impl' = 'com.evfleetmobility.complaintresolution.managerservices.service.impl'
}

Write-Host "Starting Complaint Resolution Module Refactoring..."
Write-Host "Source: $sourceDir"
Write-Host "Target: $targetBaseDir"

# Create target directory structure
if (!(Test-Path $targetBaseDir)) {
    New-Item -ItemType Directory -Path $targetBaseDir -Force | Out-Null
    Write-Host "Created target directory: $targetBaseDir"
}

# Function to create necessary directories
function Ensure-DirectoryExists {
    param([string]$Path)
    if (!(Test-Path $Path)) {
        New-Item -ItemType Directory -Path $Path -Force | Out-Null
    }
}

# Find all Java files recursively
$javaFiles = Get-ChildItem -Path $sourceDir -Filter "*.java" -Recurse -File

Write-Host "Found $($javaFiles.Count) Java files to refactor"

foreach ($file in $javaFiles) {
    # Read file content
    $content = Get-Content $file.FullName -Raw
    
    # Skip ComplaintResolutionApplication.java - we'll delete it
    if ($file.Name -eq "ComplaintResolutionApplication.java") {
        Write-Host "Skipping standalone Application class: $($file.Name)"
        continue
    }
    
    # Apply package mappings
    foreach ($oldPackage in $packageMappings.Keys) {
        $newPackage = $packageMappings[$oldPackage]
        $escapedOldPkg = [regex]::Escape($oldPackage)
        $content = $content -replace "package\s+$escapedOldPkg;", "package $newPackage;"
        $content = $content -replace "import\s+$escapedOldPkg\.", "import $newPackage."
    }
    
    # Extract package from content
    if ($content -match 'package\s+(com\.evfleetmobility[.\w]+);') {
        $packageName = $matches[1]
        $packagePath = $packageName -replace '\.', '\'
        $targetDir = Join-Path $targetBaseDir $packagePath
        
        # Ensure directory exists
        Ensure-DirectoryExists -Path $targetDir
        
        # Write refactored file
        $targetFile = Join-Path $targetDir $file.Name
        Set-Content -Path $targetFile -Value $content -Encoding UTF8
        
        Write-Host "Refactored: $($file.Name) -> $packageName"
    }
    else {
        Write-Host "Warning: Could not determine package for $($file.Name)"
    }
}

Write-Host "Refactoring complete!"
Write-Host "Next steps:"
Write-Host "1. Verify the refactored files in $targetBaseDir"
Write-Host "2. Update any remaining imports if needed"
Write-Host "3. Remove old Complaint_Resolution directory"
