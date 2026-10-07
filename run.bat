@echo off
setlocal
echo ========================================================
echo        Starting PoojaMart Toys Spring Boot Application
echo ========================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-11"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2023.1\plugins\maven\lib\maven3\bin\mvn.cmd"
if not exist "%MVN_CMD%" (
    set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.1\plugins\maven\lib\maven3\bin\mvn.cmd"
)

if exist "%MVN_CMD%" (
    echo Using Maven from: %MVN_CMD%
    "%MVN_CMD%" spring-boot:run
) else (
    echo Maven command not found in default JetBrains directory.
    echo Running with mvn if on system PATH...
    mvn spring-boot:run
)

pause
