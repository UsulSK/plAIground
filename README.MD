# PlAIground

> I make friends. They're toys. My friends are toys. I make them.
> - J.F. Sebastian

Play text based games with/against AI! We call them "replicants" here.

---

## Building

Use maven to build.

Switch to your repository folder and run:

```cmd
mvn clean install
```

# Running

Best to use OpenJDK 21.

Get it from [here](https://jdk.java.net/archive/).

After build, start **PlAIground** with:

```cmd
<PATH TO JAVA>\java -jar <PATH TO REPOSITORY>\target\plAIground-1.0-SNAPSHOT.jar
```

It needs to have the LLM started though, before you start the jar! See next chapter for that.

## LLM

**PlAIground** needs **koboldcpp** to be running for the LLM.

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

## Build WIN App

If you want to build a folder, which contains everything you need to run the project on Windows, run:
```cmd
mvn clean install -Pbuildwin
```

This will create a folder `playground_win` in the `target` folder.

This command will copy all needed files into that folder.
In the end, you can then just start the app with the `start.bat` in that folder.

### Prerequisites

To make this work, you need the files in the right place, so that maven can copy them into the target folder!
You need the following folder structure:

* `repository` (*name does not matter*): The GIT repository. Has the `pom.xml` in root. You run the maven commands here.
* `llm` (so this folder is next to the GIT repository folder)
  * `Meta-Llama-3-8B-Instruct.Q5_0.gguf` (*the LLM, see chapter [LLM](#llm)*)
  * `koboldcpp.exe` (*the LLM server, see chapter [LLM](#llm)*)
* `jdk` (*OpenJDK 21, see [Running](#running)*)

https://jdk.java.net/archive/
https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_windows-x64_bin.zip