# PlAIground

> I make friends. They're toys. My friends are toys. I make them.
> - J.F. Sebastian

Play text based games with/against AI! We call them "replicants" here.

---

## Start

This needs **koboldcpp** to be running for the LLM.

Expected port for the **koboldcpp** REST endpoint is **5001**.

[koboldcpp website](https://github.com/LostRuins/koboldcpp)

A possible setup for Windows:
- Download `koboldcpp.exe` from [here](https://github.com/LostRuins/koboldcpp/releases/tag/v1.97.4) .
- Download a LLM as explained 
 [here](https://github.com/LostRuins/koboldcpp?tab=readme-ov-file#Obtaining-a-GGUF-model) . 
 For example `Meta-Llama-3-8B-Instruct.Q5_0.gguf` from [here](https://huggingface.co/QuantFactory/Meta-Llama-3-8B-Instruct-GGUF/tree/main) .  
 Put it in the same folder as `kobold.cpp.exe` .
- In a `cmd` console, you can now start the LLM from the same folder with:  
  `koboldcpp.exe --model Meta-Llama-3-8B-Instruct.Q5_0.gguf --host 127.0.0.1 --port 5001 --chatcompletionsadapter kobold`

The LLM should run now and you can use the REST endpoint to use it!

For example with this REST call as a Powershell command:
```powershell
Invoke-RestMethod -Uri 'http://127.0.0.1:5001/api/v1/generate' -Method Post -ContentType 'application/json' -Body '{"prompt":"Say three nouns.","max_length":50,"temperature":0.7,"seed":1234}' | ConvertTo-Json -Depth 10
```
