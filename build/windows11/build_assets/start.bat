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

CD /D "%~dp0llm"

START "PlAIground LLM server" koboldcpp.exe --model llm.gguf --host 127.0.0.1 --port 5001 --quiet %GPU_FLAG%

ECHO.
ECHO Waiting for LLM server to be ready
ECHO.


:WAIT_LOOP
:: Test if the API endpoint responds with an HTTP 200 Status code
powershell -NoProfile -Command ^
    "$ProgressPreference = 'SilentlyContinue';" ^
    "try {" ^
    "   $resp = Invoke-WebRequest -Uri 'http://127.0.0.1:5001/api/v1/model' -Method Get -TimeoutSec 1 -UseBasicParsing;" ^
    "   if ($resp.StatusCode -eq 200) { exit 0 } else { exit 1 }" ^
    "} catch { exit 1 }"

IF %ERRORLEVEL% EQU 0 (
    GOTO START_GAME
) ELSE (
    :: Print a single dot visual indicator, wait 1 second, then loop back
    <NUL SET /P "=."
    TIMEOUT /T 1 /NOBREAK >NUL
    GOTO WAIT_LOOP
)

:START_GAME


ECHO.
ECHO.
ECHO === Start PlAIground ===
ECHO.
ECHO.


:: 1. Find the Process ID (PID) of the KoboldCPP process we started earlier
SET "LLM_PID="
FOR /F "tokens=2" %%A IN ('tasklist /FI "IMAGENAME eq koboldcpp.exe" /NH 2^>NUL') DO (
    SET "LLM_PID=%%A"
)

CD /D "%~dp0game"
plaiground.exe



ECHO.
ECHO.
ECHO === Exit: Close LLM ===
ECHO.
ECHO.

IF DEFINED LLM_PID (
    :: /F forces termination, /T kills any child processes it spawned
    taskkill /PID %LLM_PID% /F /T >NUL 2>&1
) ELSE (
    :: Fallback: kill by name if the PID lookup failed
    taskkill /IM koboldcpp.exe /F /T >NUL 2>&1
)

ENDLOCAL
