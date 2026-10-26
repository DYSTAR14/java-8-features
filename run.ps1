<#
  Run one lesson (or all lessons) on Windows PowerShell.
  These are the Java 21 edition of the lessons. Needs JDK 21 or newer.

  Examples:
    .\run.ps1 1        # run lesson 01
    .\run.ps1 10       # run lesson 10
    .\run.ps1 all      # run every lesson (good for a quick health check)

  The code is compiled with "--release 21".
  The original Java 8 version of every lesson is in lessons-java8\ and runs with
  .\run-java8.ps1 (compiled with "--release 8").
#>
param([Parameter(Mandatory = $true)][string]$Lesson)

$root = $PSScriptRoot
$common = Join-Path $root "lessons\common"

function Run-One($dir) {
    $main = Get-ChildItem $dir.FullName -Filter "Lesson*.java" | Select-Object -First 1
    if (-not $main) { Write-Host "No Lesson*.java in $($dir.Name)"; return $false }
    $out = Join-Path $root "out\$($dir.Name)"
    New-Item -ItemType Directory -Force $out | Out-Null
    Write-Host ""
    Write-Host "=================== $($dir.Name) ===================" -ForegroundColor Cyan
    javac --release 21 -encoding UTF-8 -sourcepath $common -d $out $main.FullName | Out-Host
    if ($LASTEXITCODE -ne 0) { return $false }
    java -cp $out ([IO.Path]::GetFileNameWithoutExtension($main.Name)) | Out-Host
    return ($LASTEXITCODE -eq 0)
}

$dirs = Get-ChildItem (Join-Path $root "lessons") -Directory | Where-Object { $_.Name -match '^\d\d-' } | Sort-Object Name

if ($Lesson -eq "all") {
    $failed = @()
    foreach ($d in $dirs) { if (-not (Run-One $d)) { $failed += $d.Name } }
    if ($failed.Count -gt 0) { Write-Host "FAILED: $($failed -join ', ')" -ForegroundColor Red; exit 1 }
    Write-Host "`nAll lessons ran OK." -ForegroundColor Green
} else {
    $prefix = $Lesson.PadLeft(2, '0')
    $dir = $dirs | Where-Object { $_.Name -like "$prefix-*" } | Select-Object -First 1
    if (-not $dir) { Write-Host "Lesson '$Lesson' not found."; exit 1 }
    if (-not (Run-One $dir)) { exit 1 }
}
