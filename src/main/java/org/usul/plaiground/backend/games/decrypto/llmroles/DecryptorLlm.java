package org.usul.plaiground.backend.games.decrypto.llmroles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.usul.plaiground.backend.games.decrypto.entities.Player;
import org.usul.plaiground.utils.FileReaderUtil;
import org.usul.plaiground.utils.StringParser;

import java.util.ArrayList;
import java.util.List;

public class DecryptorLlm extends DecryptoLlmParent {

    public List<Integer> decrypt(Player player, List<String> encryptedCode, int roundNumber) {
        String prompt = this.createPrompt(player, encryptedCode, roundNumber);

        if (System.getenv("NO_LLM_DEBUG_MODE") != null) {
            // log.info(prompt);
            log.info("Running in DEBUG MODE WITH NO LLM!");
            return new ArrayList<>(List.of(2, 1, 3));
        }

        List<Integer> guessedCode = this.useLlm(prompt, this::parseAnswer);

        return guessedCode;
    }

    private List<Integer> parseAnswer(String answer) {
        List<Integer> guessedCode = new ArrayList<>();
        try {
            String jsonPartOfAnswer = StringParser.parseJson(answer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonPartOfAnswer);
            guessedCode.add(node.get("code_for_first_digit").get("code").asInt());
            guessedCode.add(node.get("clue_for_second_digit").get("code").asInt());
            guessedCode.add(node.get("clue_for_third_digit").get("code").asInt());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return guessedCode;
    }

    private String createPrompt(Player player, List<String> clues, int roundNumber) {
        String promptTemplateGeneral = this.fileReaderUtil.readTextFile("decrypto/prompt_general");
        String promptTemplateDecrypt = this.fileReaderUtil.readTextFile("decrypto/prompt_decryptor");
        String finalPrompt = LlmPromptCreator.createPromptForDecrypt(gameState, promptTemplateGeneral,
                promptTemplateDecrypt, player, clues, roundNumber);

        return finalPrompt;
    }
}
