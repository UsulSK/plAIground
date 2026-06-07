@echo off

start powershell -NoExit -Command "Invoke-RestMethod -Uri 'http://127.0.0.1:5001/api/v1/generate' -Method Post -ContentType 'application/json' -Body '{\"prompt\":\"Say three nouns. They should be suitable for board game guess words. Comma separate them and do not say anything else.\",\"max_length\":50,\"temperature\":0.7,\"seed\":1234}' | ConvertTo-Json -Depth 10"
