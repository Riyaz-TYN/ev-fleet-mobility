# Fix the nested directory structure
$source = "src\main\java\com\evfleetmobility\complaintresolution\com\evfleetmobility\complaintresolution"
$target = "src\main\java\com\evfleetmobility\complaintresolution"

if (Test-Path $source) {
    Write-Host "Moving files from nested structure..."
    
    # Get all files from source
    $files = Get-ChildItem -Path $source -Recurse -File
    
    foreach ($file in $files) {
        $relPath = $file.FullName.Substring((Get-Item $source).FullName.Length + 1)
        $targetPath = Join-Path $target $relPath
        $targetDir = Split-Path $targetPath
        
        # Create target directory if it doesn't exist
        if (!(Test-Path $targetDir)) {
            New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
        }
        
        # Move file
        Move-Item -Path $file.FullName -Destination $targetPath -Force
        Write-Host "Moved: $relPath"
    }
    
    # Remove old nested structure
    Write-Host "Cleaning up old nested directory..."
    Remove-Item "src\main\java\com\evfleetmobility\complaintresolution\com" -Recurse -Force -ErrorAction SilentlyContinue
    
    Write-Host "Directory structure fixed!"
}
else {
    Write-Host "Source directory not found: $source"
}
