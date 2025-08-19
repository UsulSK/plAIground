package org.usul.plaiground.games.decrypto.llmroles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usul.plaiground.games.decrypto.entities.*;
import org.usul.plaiground.outbound.llm.KoboldLlmConnector;
import org.usul.plaiground.utils.FileReader;
import org.usul.plaiground.utils.StringParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EncryptorLlm extends DecryptoLlmParent {

    private static final Logger log = LoggerFactory.getLogger(EncryptorLlm.class);

    @Inject
    private FileReader fileReader;

    @Inject
    KoboldLlmConnector llm;

    public List<String> encrypt(Player player, List<Integer> code, int roundNumber) {
        String prompt = this.createPrompt(player, code, roundNumber);

        if (System.getenv("NO_LLM_DEBUG_MODE") != null) {
           // log.info(prompt);
            log.info("Running in DEBUG MODE WITH NO LLM!");
            return new ArrayList<>(List.of("clueBanana", "clueApple", "clueOrange"));
        }

        String answer = llm.chat(prompt);

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
        String promptTemplateGeneral = this.fileReader.readTextFile("decrypto/prompt_general");
        String promptTemplateEncrypt = this.fileReader.readTextFile("decrypto/prompt_encryptor");
        String finalPrompt = promptTemplateGeneral + "\n" + promptTemplateEncrypt;

        finalPrompt = this.getPromptForGeneralTemplate(finalPrompt, player, roundNumber);
        finalPrompt = finalPrompt.replace("{secret_words}", this.getSecretWordsLlmSerialization(player));
        finalPrompt = finalPrompt.replace("{code}", this.getCodeText(code));
        finalPrompt = finalPrompt.replace("{used_clues}", this.getAllUsedClues(player));

        return finalPrompt;
    }

    private String getAllUsedClues(Player player) {
        Team teamOfPlayer = this.gameWorld.getTeamOfPlayer(player);
        List<String> usedCodesForDigit1 = new ArrayList<>();
        List<String> usedCodesForDigit2 = new ArrayList<>();
        List<String> usedCodesForDigit3 = new ArrayList<>();
        for (Round round : this.gameWorld.getGameLog().getRounds()) {
            TeamRound teamRound = round.getTeamInfo().get(teamOfPlayer.getName());
            if (teamRound == null) {
                break;
            }
            List<String> encCode = teamRound.getEncryptedCode();
            if (encCode.isEmpty()) {
                continue;
            }
            usedCodesForDigit1.add(encCode.get(0));
            usedCodesForDigit2.add(encCode.get(1));
            usedCodesForDigit3.add(encCode.get(2));
        }

        if( usedCodesForDigit1.isEmpty() ) {
            return "no clues have been given yet";
        }

        return "for first digit: " + usedCodesForDigit1.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).get()
                + "; for second digit: " + usedCodesForDigit2.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).get()
                + "; for third digit: " + usedCodesForDigit3.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).get();
    }
}
