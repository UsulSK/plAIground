@echo off
title plAIground Automated Builder
cls

echo.
echo Building plAIground for Windows 11
echo.

:: this is for text coloring
for /F %%A in ('echo prompt $E ^| cmd') do set "ESC=%%A"
set "RED=%ESC%[31m"
set "GREEN=%ESC%[32m"
set "YELLOW=%ESC%[33m"
set "RESET=%ESC%[0m"

:: ==========================================
:: CONFIGURATION
:: ==========================================
:: %~dp0 refers to the folder containing this batch file (the build folder)

set "SCRIPT_DIR=%~dp0"
set "GODOT_EXE=%SCRIPT_DIR%..\..\..\godot\godot_console.exe"
set "PROJECT_DIR=%SCRIPT_DIR%..\..\app_src"
set "LLM_DIR=%SCRIPT_DIR%..\..\..\llm"
set "OUTPUT_DIR=%SCRIPT_DIR%target"
set "OUTPUT_NAME=plaiground.exe"
set "EXPORT_PRESET=Windows Desktop"

:: ==========================================
:: VALIDATION 
:: ==========================================

echo.
echo Validating configuration and structure...
echo.

:: Verify Godot executable exists
if not exist "%GODOT_EXE%" (
    echo %RED%[ERROR] Godot engine not found.%RESET%
    echo Expected path: %GODOT_EXE%
    pause
    exit /b
)

:: Verify the project folder exists
if not exist "%PROJECT_DIR%\project.godot" (
    echo %RED%[ERROR] 'project.godot' file not found.%RESET%
    echo Expected path: %PROJECT_DIR%\project.godot
    pause
    exit /b
)

:: Verify the LLM server exists
if not exist "%LLM_DIR%\koboldcpp.exe" (
    echo %RED%[ERROR] 'koboldcpp.exe' file not found.%RESET%
    echo Expected path: %LLM_DIR%\koboldcpp.exe
    pause
    exit /b
)

:: Verify the LLM exists
if not exist "%LLM_DIR%\llm.gguf" (
    echo %RED%[ERROR] 'llm.gguf' file not found.%RESET%
    echo Expected path: %LLM_DIR%\llm.gguf. Put the llm gguf file there and re-name it accordingly.
    pause
    exit /b
)

echo %GREEN%VALIDATION SUCCESS%RESET%

:: ==========================================
:: COPY FILES TO TARGET
:: ==========================================

echo.
echo Preparing output directory...
echo.

if exist "%OUTPUT_DIR%" (
    echo Cleaning existing target folder...
    rmdir /s /q "%OUTPUT_DIR%"
)
mkdir "%OUTPUT_DIR%"
mkdir "%OUTPUT_DIR%\game"
mkdir "%OUTPUT_DIR%\llm"
echo Created directories

copy /Y "%SCRIPT_DIR%build_assets\start.bat" "%SCRIPT_DIR%target\start.bat"
copy /Y "%LLM_DIR%\koboldcpp.exe" "%SCRIPT_DIR%target\llm\koboldcpp.exe"
copy /Y "%LLM_DIR%\llm.gguf" "%SCRIPT_DIR%target\llm\llm.gguf"

:: ==========================================
:: BUILD
:: ==========================================

echo.
echo build godot app
echo.

cd /d "%PROJECT_DIR%"

:: Print the command out to the console
echo ==========================================
echo Running build command:
echo "%GODOT_EXE%" --headless --export-release "%EXPORT_PRESET%" "%OUTPUT_DIR%\game\%OUTPUT_NAME%"
echo ==========================================
echo.

:: Execute headless export
"%GODOT_EXE%" --headless --export-release "%EXPORT_PRESET%" "%OUTPUT_DIR%\game\%OUTPUT_NAME%"

echo.

if %ERRORLEVEL% equ 0 (
    echo ==========================================
    echo %GREEN%[SUCCESS] Build completed flawlessly! %RESET%
    echo Location: %OUTPUT_DIR%
    echo ==========================================
) else (
    echo ==========================================
    echo %RED%[ERROR] Godot compilation failed.%RESET%
    echo ==========================================
)
echo.

pause