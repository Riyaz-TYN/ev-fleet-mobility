# Comprehensive refactoring script for Complaint Resolution module

$sourceDir = "src\main\java\com\evfleetmobility\Complaint_Resolution"
$targetBaseDir = "src\main\java\com\evfleetmobility\complaintresolution"

# Mapping of old directories to new structure
$dirMappings = @{
    "auditlog" = "complaintservices\audit"
    "escalation" = "complaintservices\escalation"
    "notification" = "complaintservices\notification"
    "complaint" = "complaintservices\complaint"
    "vendor" = "complaintservices\vendor"
    "manager" = "managerservices"
}

# Package name mappings
$packageMappings = @(
    @{ old = "package com.complaint_resolution.auditlog"; new = "package com.evfleetmobility.complaintresolution.complaintservices.audit" },
    @{ old = "package com.complaint_resolution.escalation"; new = "package com.evfleetmobility.complaintresolution.complaintservices.escalation" },
    @{ old = "package com.complaint_resolution.notification"; new = "package com.evfleetmobility.complaintresolution.complaintservices.notification" },
    @{ old = "package com.complaint_resolution.complaint"; new = "package com.evfleetmobility.complaintresolution.complaintservices.complaint" },
    @{ old = "package com.complaint_resolution.vendor"; new = "package com.evfleetmobility.complaintresolution.complaintservices.vendor" },
    @{ old = "package com.complaint_resolution.manager"; new = "package com.evfleetmobility.complaintresolution.managerservices" },
    @{ old = "import com.complaint_resolution.auditlog"; new = "import com.evfleetmobility.complaintresolution.complaintservices.audit" },
    @{ old = "import com.complaint_resolution.escalation"; new = "import com.evfleetmobility.complaintresolution.complaintservices.escalation" },
    @{ old = "import com.complaint_resolution.notification"; new = "import com.evfleetmobility.complaintresolution.complaintservices.notification" },
    @{ old = "import com.complaint_resolution.complaint"; new = "import com.evfleetmobility.complaintresolution.complaintservices.complaint" },
    @{ old = "import com.complaint_resolution.vendor"; new = "import com.evfleetmobility.complaintresolution.complaintservices.vendor" },
    @{ old = "import com.complaint_resolution.manager"; new = "import com.evfleetmobility.complaintresolution.managerservices" }
)

Write-Host "Starting comprehensive refactoring..."
Write-Host "Source: $sourceDir"
Write-Host "Target: $targetBaseDir"

# Create target base directory
New-Item -ItemType Directory -Path $targetBaseDir -Force | Out-Null

# Process each directory
foreach ($sourceSubDir in Get-ChildItem -Path $sourceDir -Directory) {
    $subDirName = $sourceSubDir.Name
    
    if ($dirMappings.ContainsKey($subDirName)) {
        $newSubPath = $dirMappings[$subDirName]
        $targetSubDir = Join-Path $targetBaseDir $newSubPath
        
        Write-Host ""
        Write-Host "Processing: $subDirName -> $newSubPath"
        
        # Create target subdirectory
        New-Item -ItemType Directory -Path $targetSubDir -Force | Out-Null
        
        # Copy directory structure
        Copy-Item -Path (Join-Path $sourceDir $subDirName "*") -Destination $targetSubDir -Recurse -Force
        
        Write-Host "Copied: $subDirName"
    }
}

# Now update all package declarations and imports in the copied files
Write-Host ""
Write-Host "Updating package names and imports..."

$javaFiles = Get-ChildItem -Path $targetBaseDir -Filter "*.java" -Recurse -File

foreach ($file in $javaFiles) {
    Write-Host "Updating: $($file.Name)"
    $content = Get-Content $file.FullName -Raw
    
    # Apply all package mappings
    foreach ($mapping in $packageMappings) {
        $content = $content -replace [regex]::Escape($mapping.old), $mapping.new
    }
    
    # Write updated content
    Set-Content -Path $file.FullName -Value $content -Encoding UTF8
}

Write-Host ""
Write-Host "Refactoring complete!"
Write-Host "Refactored module location: $targetBaseDir"
