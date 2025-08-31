package org.usul.plaiground.backend.games.decrypto.llmroles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.usul.plaiground.backend.games.decrypto.entities.Player;
import org.usul.plaiground.backend.games.decrypto.entities.Team;
import org.usul.plaiground.utils.StringParser;

import java.util.List;
import java.util.stream.Collectors;

public class EncryptorLlm extends DecryptoLlmParent {

    public EncryptResponse encrypt(Player player, List<Integer> code, int roundNumber) {
        String prompt = this.createPrompt(player, code, roundNumber);

        if (System.getenv("NO_LLM_DEBUG_MODE") != null) {
            log.info("no_llm_mode");
            log.info("prompt:\n\n" + prompt + "\n\n");
            EncryptResponse response = new EncryptResponse();
            response.getClues().addAll(List.of("clueBanana", "clueApple", "clueOrange"));
            response.getReasonsTeammate().addAll(List.of("reason1", "reason2", "reason3"));
            response.getReasonsOpponent().addAll(List.of("reasonOpponent1", "reasonOpponent2", "reasonOpponent3"));

            return response;
        }

        return this.useLlm(prompt, this::parseAnswer);
    }

    private EncryptResponse parseAnswer(String answer) {
        EncryptResponse response = new EncryptResponse();

        try {
            String jsonPartOfAnswer = StringParser.parseJson(answer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonPartOfAnswer);
            response.getClues().add(node.get("clue_for_first_digit_of_code").get("clue").asText());
            response.getClues().add(node.get("clue_for_second_digit_of_code").get("clue").asText());
            response.getClues().add(node.get("clue_for_third_digit_of_code").get("clue").asText());
            response.getReasonsTeammate().add(node.get("clue_for_first_digit_of_code").get("reason_teammate").asText());
            response.getReasonsTeammate().add(node.get("clue_for_second_digit_of_code").get("reason_teammate").asText());
            response.getReasonsTeammate().add(node.get("clue_for_third_digit_of_code").get("reason_teammate").asText());

            if (node.get("clue_for_first_digit_of_code").get("reason_opponent") != null) {
                response.getReasonsOpponent().add(node.get("clue_for_first_digit_of_code").get("reason_opponent").asText());
                response.getReasonsOpponent().add(node.get("clue_for_second_digit_of_code").get("reason_opponent").asText());
                response.getReasonsOpponent().add(node.get("clue_for_third_digit_of_code").get("reason_opponent").asText());
            }
        } catch (Exception e) {
            log.error("encrypt_parse_answer_error", e);
            throw new RuntimeException(e);
        }

        return response;
    }

    private String createPrompt(Player player, List<Integer> code, int roundNumber) {
        String finalPrompt;

        if (roundNumber == 0) {
            finalPrompt = this.fileReaderUtil.readTextFile("decrypto/prompt_encryptor_first_round");
        } else {
            finalPrompt = this.fileReaderUtil.readTextFile("decrypto/prompt_encryptor");
        }

        finalPrompt = this.replaceHistory(finalPrompt, player, code);
        finalPrompt = this.replaceCode(finalPrompt, code);
        finalPrompt = this.replaceSecretWords(finalPrompt, player);
        finalPrompt = this.replaceReferringSecretWords(finalPrompt, player, code);
        finalPrompt = this.replaceOpponentReason(finalPrompt, player, 1, code);
        finalPrompt = this.replaceOpponentReason(finalPrompt, player, 2, code);
        finalPrompt = this.replaceOpponentReason(finalPrompt, player, 3, code);

        return finalPrompt;
    }

    private String replaceOpponentReason(String prompt, Player player, int codePos, List<Integer> code) {
        Team team = this.gameState.getTeamOfPlayer(player);
        int codeDigit = code.get(codePos - 1);

        String hintReplacementText = "";

        List<String> pastCluesForCodeDigit = this.gameState.getGameLog().getPastCluesForCodeDigit(codeDigit, team);

        if (pastCluesForCodeDigit.isEmpty()) {
            hintReplacementText = ".";
        } else if (pastCluesForCodeDigit.size() == 1) {
            hintReplacementText = ", even though they know the past clue '" + pastCluesForCodeDigit.getFirst() + "'.";
        } else {
            String pastCluesText = pastCluesForCodeDigit.stream().map(s -> "'" + s + "'").collect(Collectors.joining(" and "));
            hintReplacementText = ", even though they know the past clues " + pastCluesText + ".";
        }

        prompt = prompt.replace("{OPPONENT_REASON_" + codePos + "}", hintReplacementText);

        return prompt;
    }

    private String replaceHistory(String prompt, Player player, List<Integer> code) {
        Team team = this.gameState.getTeamOfPlayer(player);
        String clueHistoryReplacementText = "";

        for (Integer codeDigit : code) {
            List<String> pastCluesForCodeDigit = this.gameState.getGameLog().getPastCluesForCodeDigit(codeDigit, team);

            if (pastCluesForCodeDigit.isEmpty()) {
                continue;
            }

            if (pastCluesForCodeDigit.size() == 1) {
                clueHistoryReplacementText += "They know this past clue refers to ";
            } else {
                clueHistoryReplacementText += "They know these past " + pastCluesForCodeDigit.size() + " clues refer to ";
            }

            clueHistoryReplacementText += "\"" + codeDigit + "\" ";
            String referringSecretWord = team.getKeywords().get(codeDigit - 1);
            clueHistoryReplacementText += "(which is the secret word \"" + referringSecretWord + "\"): ";

            String pastCluesText = pastCluesForCodeDigit.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "));
            clueHistoryReplacementText += pastCluesText;

            clueHistoryReplacementText += ".\n";
        }

        prompt = prompt.replace("{CLUE_HISTORY}", clueHistoryReplacementText);

        return prompt;
    }
}
