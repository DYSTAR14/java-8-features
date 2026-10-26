<#
  Run a "modern" lesson (Java 9 to 21 features) on Windows PowerShell.
  These lessons need JDK 21 (or newer). They do NOT compile as Java 8.

  Examples:
    .\run-modern.ps1 1      # Java 9-11
    .\run-modern.ps1 2      # Java 12-17
    .\run-modern.ps1 3      # Java 18-21
    .\run-modern.ps1 all
#>
param([Parameter(Mandatory = $true)][string]$Lesson)

$root = $PSScriptRoot
$common = Join-Path $root "lessons\common"

function Run-One($dir) {
    $main = Get-ChildItem $dir.FullName -Filter "Modern*.java" | Select-Object -First 1
    if (-not $main) { Write-Host "No Modern*.java in $($dir.Name)"; return $false }
    $out = Join-Path $root "out\modern\$($dir.Name)"
    New-Item -ItemType Directory -Force $out | Out-Null
    Write-Host ""
    Write-Host "=================== modern/$($dir.Name) ===================" -ForegroundColor Cyan
    javac --release 21 -encoding UTF-8 -sourcepath $common -d $out $main.FullName | Out-Host
    if ($LASTEXITCODE -ne 0) { return $false }
    java -cp $out ([IO.Path]::GetFileNameWithoutExtension($main.Name)) | Out-Host
    return ($LASTEXITCODE -eq 0)
}

$dirs = Get-ChildItem (Join-Path $root "modern") -Directory | Where-Object { $_.Name -match '^\d\d-' } | Sort-Object Name

if ($Lesson -eq "all") {
    $failed = @()
    foreach ($d in $dirs) { if (-not (Run-One $d)) { $failed += $d.Name } }
    if ($failed.Count -gt 0) { Write-Host "FAILED: $($failed -join ', ')" -ForegroundColor Red; exit 1 }
    Write-Host "`nAll modern lessons ran OK." -ForegroundColor Green
} else {
    $prefix = $Lesson.PadLeft(2, '0')
    $dir = $dirs | Where-Object { $_.Name -like "$prefix-*" } | Select-Object -First 1
    if (-not $dir) { Write-Host "Modern lesson '$Lesson' not found."; exit 1 }
    if (-not (Run-One $dir)) { exit 1 }
}
