@echo off
setlocal
title PoojaMart - Instant Global Public Link Generator
cls
echo ========================================================
echo        PoojaMart - Instant Global Link Generator
echo ========================================================
echo.
echo Make sure PoojaMart is running on localhost:8080 (via run.bat).
echo.
echo Select tunnel method:
echo   [1] Direct SSH Tunnel (No install needed, instant global URL)
echo   [2] LocalTunnel (Via npx)
echo.
set /p CHOICE="Enter choice (1 or 2, default is 1): "
if "%CHOICE%"=="" set CHOICE=1

if "%CHOICE%"=="1" (
    echo.
    echo --------------------------------------------------------
    echo Connecting to localhost.run tunnel...
    echo Your public HTTPS link will appear below within 5 seconds.
    echo Keep this window OPEN while sharing the link!
    echo Press Ctrl+C to stop sharing anytime.
    echo --------------------------------------------------------
    echo.
    ssh -o StrictHostKeyChecking=no -R 80:localhost:8080 nokey@localhost.run
)

if "%CHOICE%"=="2" (
    echo.
    echo --------------------------------------------------------
    echo Starting LocalTunnel via npx...
    echo Keep this window OPEN while sharing the link!
    echo Press Ctrl+C to stop sharing anytime.
    echo --------------------------------------------------------
    echo.
    npx -y localtunnel --port 8080
)

pause
