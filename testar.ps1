$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force 'target/test-classes', 'evidencias' | Out-Null
    $fontes = @(Get-ChildItem 'src/main/java' -Recurse -Filter '*.java' | ForEach-Object FullName)
    & javac -encoding UTF-8 -d target/test-classes @fontes testes/VerificarAplicacao.java
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar os testes.' }
    & java '-Dfile.encoding=UTF-8' -cp target/test-classes VerificarAplicacao 2>&1 |
        Tee-Object -FilePath evidencias/testes-executados.txt
    if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes.' }
} finally { Pop-Location }
