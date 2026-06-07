@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

ECHO.
ECHO.
ECHO === Start LLM ===
ECHO.
ECHO.


:: Fetch the GPU name using PowerShell (safer and cleaner than wmic)
SET "GPU_NAME="
FOR /F "tokens=*" %%I IN ('powershell -NoProfile -Command "Get-CimInstance Win32_VideoController | Select-Object -ExpandProperty Name"') DO (
    SET "GPU_NAME=!GPU_NAME! %%I"
)

ECHO Detected GPU(s): %GPU_NAME%
ECHO.

:: Determine the correct KoboldCPP backend and layer count
SET "GPU_FLAG="
ECHO %GPU_NAME% | FINDSTR /I "NVIDIA" >NUL
IF %ERRORLEVEL% EQU 0 (
    SET "GPU_FLAG=--usecublas --gpulayers 33"
    ECHO Status: NVIDIA detected. Enabling CUDA acceleration with full layers.
    GOTO LAUNCH
)

ECHO %GPU_NAME% | FINDSTR /I "AMD" >NUL
IF %ERRORLEVEL% EQU 0 (
    SET "GPU_FLAG=--usevulkan --gpulayers 33"
    ECHO Status: AMD detected. Enabling Vulkan acceleration with full layers.
    GOTO LAUNCH
)

ECHO.
ECHO Status: No dedicated NVIDIA or AMD GPU found. Defaulting to CPU mode.
ECHO.

:LAUNCH

CD /D "%~dp0..\llm"

CALL koboldcpp.exe --model llm.gguf --host 127.0.0.1 --port 5001 %GPU_FLAG%

ECHO.
ECHO.
ECHO === Done ===
ECHO.
ECHO.

PAUSE
ENDLOCAL
