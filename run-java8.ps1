<#
  Run one ORIGINAL Java 8 lesson (or all) from lessons-java8 on Windows PowerShell.

  Examples:
    .\run.ps1 1        # run lesson 01
    .\run.ps1 10       # run lesson 10
    .\run.ps1 all      # run every lesson (good for a quick health check)

  The code is compiled with "--release 8", so if you accidentally use a
  feature newer than Java 8, the compiler will tell you. Needs JDK 8 or newer.
#>
param([Parameter(Mandatory = $true)][string]$Lesson)

$root = $PSScriptRoot
$common = Join-Path $root "lessons-java8\common"

function Run-One($dir) {
    $main = Get-ChildItem $dir.FullName -Filter "Lesson*.java" | Select-Object -First 1
    if (-not $main) { Write-Host "No Lesson*.java in $($dir.Name)"; return $false }
    $out = Join-Path $root "out\java8\$($dir.Name)"
    New-Item -ItemType Directory -Force $out | Out-Null
    Write-Host ""
    Write-Host "=================== $($dir.Name) ===================" -ForegroundColor Cyan
    javac --release 8 -Xlint:-options -encoding UTF-8 -sourcepath $common -d $out $main.FullName | Out-Host
    if ($LASTEXITCODE -ne 0) { return $false }
    java -cp $out ([IO.Path]::GetFileNameWithoutExtension($main.Name)) | Out-Host
    return ($LASTEXITCODE -eq 0)
}

$dirs = Get-ChildItem (Join-Path $root "lessons-java8") -Directory | Where-Object { $_.Name -match '^\d\d-' } | Sort-Object Name

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
