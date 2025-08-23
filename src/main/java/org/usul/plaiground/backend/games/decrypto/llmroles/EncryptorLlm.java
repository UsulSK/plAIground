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

    @Inject
    private FileReaderUtil fileReaderUtil;

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
        String finalPrompt = promptTemplateGeneral + "\n" + promptTemplateEncrypt;

        finalPrompt = this.getPromptForGeneralTemplate(finalPrompt, player, roundNumber);
        finalPrompt = finalPrompt.replace("{secret_words}", this.getSecretWordsLlmSerialization(player));
        finalPrompt = finalPrompt.replace("{code}", this.getCodeText(code));
        finalPrompt = finalPrompt.replace("{used_clues}", this.getAllUsedClues(player));

        return finalPrompt;
    }

    private String getAllUsedClues(Player player) {
        Team teamOfPlayer = this.gameState.getTeamOfPlayer(player);
        List<String> usedCodesForDigit1 = new ArrayList<>();
        List<String> usedCodesForDigit2 = new ArrayList<>();
        List<String> usedCodesForDigit3 = new ArrayList<>();
        for (Round round : this.gameState.getGameLog().getRounds()) {
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
