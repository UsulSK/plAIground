package org.usul.plaiground.backend.games.decrypto.llmroles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.usul.plaiground.backend.games.decrypto.entities.Player;
import org.usul.plaiground.backend.games.decrypto.entities.Round;
import org.usul.plaiground.backend.games.decrypto.entities.Team;
import org.usul.plaiground.backend.games.decrypto.entities.TeamRound;
import org.usul.plaiground.utils.FileReaderUtil;
import org.usul.plaiground.utils.StringParser;

import java.util.ArrayList;
import java.util.List;

public class EncryptorLlm extends DecryptoLlmParent {

    public List<String> encrypt(Player player, List<Integer> code, int roundNumber) {
        String prompt = this.createPrompt(player, code, roundNumber);

        if (System.getenv("NO_LLM_DEBUG_MODE") != null) {
           // log.info(prompt);
            log.info("Running in DEBUG MODE WITH NO LLM!");
            return new ArrayList<>(List.of("clueBanana", "clueApple", "clueOrange"));
        }

        List<String> clues = this.useLlm(prompt, this::parseAnswer);

        return clues;
    }

    private List<String> parseAnswer(String answer) {
        List<String> clues = new ArrayList<>();
        try {
            String jsonPartOfAnswer = StringParser.parseJson(answer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonPartOfAnswer);
            clues.add(node.get("clue_for_first_digit").get("clue").asText());
            clues.add(node.get("clue_for_second_digit").get("clue").asText());
            clues.add(node.get("clue_for_third_digit").get("clue").asText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clues;
    }

    private String createPrompt(Player player, List<Integer> code, int roundNumber) {
        String promptTemplateGeneral = this.fileReaderUtil.readTextFile("decrypto/prompt_general");
        String promptTemplateEncrypt = this.fileReaderUtil.readTextFile("decrypto/prompt_encryptor");
        String finalPrompt = LlmPromptCreator.createPromptForEncrypt(gameState, promptTemplateGeneral,
                promptTemplateEncrypt, player, code, roundNumber);

        return finalPrompt;
    }
}
