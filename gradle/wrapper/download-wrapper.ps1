# Baixa apenas o wrapper oficial. Verificação SHA-256 publicada pelo Gradle.
$ErrorActionPreference = 'Stop'
$url = 'https://services.gradle.org/distributions/gradle-8.14-wrapper.jar'
$jar = Join-Path $PSScriptRoot 'gradle-wrapper.jar'
$expected = '7d3a4ac4de1c32b59bc6a4eb8ecb8e612ccd0cf1ae1e99f66902da64df296172'
try {
    Write-Host 'Obtendo Gradle Wrapper 8.14 oficial...'
    Invoke-WebRequest -Uri $url -OutFile $jar -UseBasicParsing
    $hash = (Get-FileHash -Algorithm SHA256 $jar).Hash.ToLowerInvariant()
    if ($hash -ne $expected) {
        throw "SHA-256 invalido: $hash"
    }
} catch {
    Remove-Item $jar -Force -ErrorAction SilentlyContinue
    Write-Error "Falha ao baixar/verificar o Gradle Wrapper. $_"
    exit 1
}
Write-Host 'Gradle Wrapper verificado.'
