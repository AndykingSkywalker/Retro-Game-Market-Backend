@echo off
setlocal
set BASEDIR=%~dp0
set WRAPPER_DIR=%BASEDIR%\.mvn\wrapper
set JAR=%WRAPPER_DIR%\maven-wrapper.jar

if not exist "%JAR%" (
  echo Downloading Maven wrapper...
  if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
  set WRAPPER_VERSION=3.3.2
  set URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/%WRAPPER_VERSION%/maven-wrapper-%WRAPPER_VERSION%.jar
  powershell -NoProfile -ExecutionPolicy Bypass -Command "(New-Object Net.WebClient).DownloadFile('%URL%','%JAR%')" || exit /b 1
)

java -Dmaven.multiModuleProjectDirectory="%BASEDIR%" -jar "%JAR%" %*

