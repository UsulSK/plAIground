@ECHO OFF

REM This file starts PlAIground on Windows!


ECHO.
ECHO.
ECHO === starting the LLM ===
ECHO.
ECHO.

CD bck

START "" /MIN cmd /C "koboldcpp.exe --model Meta-Llama-3-8B-Instruct.Q5_0.gguf --host 127.0.0.1 --port 5001 --chatcompletionsadapter kobold"


ECHO.
ECHO.
ECHO === waiting until the LLM server has started ===


:CHECK_SERVER
curl --silent http://127.0.0.1:5001/info >nul 2>&1
IF %ERRORLEVEL% EQU 0 (
    ECHO Server is up!
    GOTO SERVER_READY
)

ECHO Server not ready yet (checked http://127.0.0.1:5001/info). Waiting ...
TIMEOUT /T 2 /NOBREAK >nul
GOTO CHECK_SERVER

:SERVER_READY


ECHO.
ECHO === Server is ready! Starting PlAIground client! ===
ECHO.


CD ..

CALL bck\jdk\bin\java.exe -jar bck\plaiground.jar



ECHO.
ECHO === Shutting down! ===
ECHO.

PAUSE


REM kill the server
TASKKILL /IM koboldcpp.exe /F


PAUSE

ECHO.
ECHO.
ECHO === Done ===
ECHO.
ECHO.