@echo off
echo ========================================================
echo Compiling Courier Management System (KTU S3 OOP Project)
echo ========================================================
if not exist "bin" mkdir bin
javac -cp "lib/mysql-connector-j-8.4.0.jar" -d bin src/com/courier/model/*.java src/com/courier/exception/*.java src/com/courier/util/*.java src/com/courier/db/*.java src/com/courier/gui/*.java src/com/courier/main/*.java
if %errorlevel% equ 0 (
    echo.
    echo Compilation successful! Classes compiled to 'bin' folder.
) else (
    echo.
    echo Compilation failed. Please check errors above.
)
pause
