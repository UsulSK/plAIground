package org.usul.plaiground.games.decrypto.llmroles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usul.plaiground.games.decrypto.entities.GameWorld;
import org.usul.plaiground.games.decrypto.entities.Player;
import org.usul.plaiground.outbound.llm.KoboldLlmConnector;
import org.usul.plaiground.utils.FileReader;
import org.usul.plaiground.utils.StringParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DecryptorLlm extends DecryptoLlmParent {

    private static final Logger log = LoggerFactory.getLogger(DecryptorLlm.class);

    private GameWorld gameWorld;

    @Inject
    private FileReader fileReader;

    @Inject
    KoboldLlmConnector llm;

    public List<Integer> decrypt(Player player, List<String> encryptedCode, int roundNumber) {
        String prompt = this.createPrompt(player, encryptedCode, roundNumber);

        if (System.getenv("NO_LLM_DEBUG_MODE") != null) {
            // log.info(prompt);
            log.info("Running in DEBUG MODE WITH NO LLM!");
            return new ArrayList<>(List.of(2, 1, 3));
        }

        String answer = llm.chat(prompt);

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
        String promptTemplateGeneral = this.fileReader.readTextFile("decrypto/prompt_general");
        String promptTemplateEncrypt = this.fileReader.readTextFile("decrypto/prompt_decryptor");
        String finalPrompt = promptTemplateGeneral + "\n" + promptTemplateEncrypt;
        finalPrompt = this.getPromptForGeneralTemplate(finalPrompt, player, roundNumber);
        finalPrompt = finalPrompt.replace("{secret_words}", this.getSecretWordsLlmSerialization(player));
        finalPrompt = finalPrompt.replace("{clues}", this.getCluesLlmSerialization(clues));

        return finalPrompt;
    }

    private String getCluesLlmSerialization(List<String> clues) {
        String result = clues.stream()
                .map(s -> "[" + s + "]")
                .collect(Collectors.joining(" "));
        return result;
    }
}
