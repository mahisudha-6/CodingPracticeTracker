@echo off
echo ===================================================
echo   Coding Practice Tracker Compiler and Runner
echo ===================================================
echo.

set "JAR_FILE=lib\mysql-connector-j-9.0.0.jar"

if not exist "%JAR_FILE%" (
    echo Error: MySQL JDBC Connector JAR was not found at %JAR_FILE%
    echo Please make sure you have placed the mysql-connector-j-9.0.0.jar file in the 'lib' folder.
    pause
    exit /b 1
)

echo Database Driver Found: %JAR_FILE%
echo.

echo Compiling all Java classes...
if not exist bin mkdir bin

javac -cp "%JAR_FILE%;src" -d bin src\model\*.java src\exception\*.java src\util\*.java src\dao\*.java src\service\*.java src\ui\*.java src\main\*.java

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation Successful! Launching application...
echo.

java -Xmx512m -Xms128m -cp "bin;%JAR_FILE%" main.MainApp

if errorlevel 1 (
    echo.
    echo Application exited with an error.
)
pause
