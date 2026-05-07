# Refactor files in the temp directory

$tempDir = "src\main\java\com\evfleetmobility\complaintresolution_temp"
$targetBaseDir = "src\main\java\com\evfleetmobility\complaintresolution"

Write-Host "Refactoring files in temp directory..."

# Get all Java files
$javaFiles = Get-ChildItem -Path $tempDir -Filter "*.java" -Recurse -File

foreach ($file in $javaFiles) {
    # Skip ComplaintResolutionApplication.java
    if ($file.Name -eq "ComplaintResolutionApplication.java") {
        Write-Host "Skipping application class: $($file.Name)"
        continue
    }
    
    $content = Get-Content $file.FullName -Raw
    
    # Replace all package declarations
    $content = $content -replace "package com\.complaint_resolution\.auditlog", "package com.evfleetmobility.complaintresolution.complaintservices.audit"
    $content = $content -replace "package com\.complaint_resolution\.escalation", "package com.evfleetmobility.complaintresolution.complaintservices.escalation"
    $content = $content -replace "package com\.complaint_resolution\.notification", "package com.evfleetmobility.complaintresolution.complaintservices.notification"
    $content = $content -replace "package com\.complaint_resolution\.complaint", "package com.evfleetmobility.complaintresolution.complaintservices.complaint"
    $content = $content -replace "package com\.complaint_resolution\.vendor", "package com.evfleetmobility.complaintresolution.complaintservices.vendor"
    $content = $content -replace "package com\.complaint_resolution\.manager", "package com.evfleetmobility.complaintresolution.managerservices"
    
    # Replace all imports
    $content = $content -replace "import com\.complaint_resolution\.auditlog", "import com.evfleetmobility.complaintresolution.complaintservices.audit"
    $content = $content -replace "import com\.complaint_resolution\.escalation", "import com.evfleetmobility.complaintresolution.complaintservices.escalation"
    $content = $content -replace "import com\.complaint_resolution\.notification", "import com.evfleetmobility.complaintresolution.complaintservices.notification"
    $content = $content -replace "import com\.complaint_resolution\.complaint", "import com.evfleetmobility.complaintresolution.complaintservices.complaint"
    $content = $content -replace "import com\.complaint_resolution\.vendor", "import com.evfleetmobility.complaintresolution.complaintservices.vendor"
    $content = $content -replace "import com\.complaint_resolution\.manager", "import com.evfleetmobility.complaintresolution.managerservices"
    
    # Write back updated content
    Set-Content -Path $file.FullName -Value $content -Encoding UTF8
}

Write-Host "Refactoring complete!"
Write-Host ""
Write-Host "Now reorganizing directory structure..."

# Create target directory structure
New-Item -ItemType Directory -Path "$targetBaseDir\complaintservices\complaint" -Force | Out-Null
New-Item -ItemType Directory -Path "$targetBaseDir\complaintservices\vendor" -Force | Out-Null
New-Item -ItemType Directory -Path "$targetBaseDir\complaintservices\audit" -Force | Out-Null
New-Item -ItemType Directory -Path "$targetBaseDir\complaintservices\escalation" -Force | Out-Null
New-Item -ItemType Directory -Path "$targetBaseDir\complaintservices\notification" -Force | Out-Null
New-Item -ItemType Directory -Path "$targetBaseDir\managerservices" -Force | Out-Null

# Move files to their new locations
$dirMappings = @{
    "$tempDir\complaint" = "$targetBaseDir\complaintservices\complaint"
    "$tempDir\vendor" = "$targetBaseDir\complaintservices\vendor"
    "$tempDir\auditlog" = "$targetBaseDir\complaintservices\audit"
    "$tempDir\escalation" = "$targetBaseDir\complaintservices\escalation"
    "$tempDir\notification" = "$targetBaseDir\complaintservices\notification"
    "$tempDir\manager" = "$targetBaseDir\managerservices"
}

foreach ($from in $dirMappings.Keys) {
    $to = $dirMappings[$from]
    if (Test-Path $from) {
        Get-ChildItem -Path $from -Recurse -File | ForEach-Object {
            $relPath = $_.FullName.Substring($from.Length + 1)
            $targetPath = Join-Path $to $relPath
            $targetDir = Split-Path $targetPath
            
            if (!(Test-Path $targetDir)) {
                New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
            }
            
            Copy-Item -Path $_.FullName -Destination $targetPath -Force
        }
        Write-Host "Moved: $($from.Split('\')[-1])"
    }
}

Write-Host "Reorganization complete!"
Write-Host "Target: $targetBaseDir"
