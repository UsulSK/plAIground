package org.usul.plaiground.backend.games.decrypto.llmroles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.usul.plaiground.backend.games.decrypto.entities.Player;
import org.usul.plaiground.backend.games.decrypto.entities.Team;
import org.usul.plaiground.utils.FileReaderUtil;
import org.usul.plaiground.utils.StringParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InterceptorLlm extends DecryptoLlmParent {

    public List<Integer> intercept(Player player, List<String> encryptedCode, int roundNumber) {
        String prompt = this.createPrompt(player, encryptedCode, roundNumber);

        if (System.getenv("NO_LLM_DEBUG_MODE") != null) {
            log.info("no_llm_mode");
            log.info(prompt);
            return new ArrayList<>(List.of(4, 3, 2));
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
            guessedCode.add(node.get("guess_for_first_clue").get("guessed_position").asInt());
            guessedCode.add(node.get("guess_for_second_clue").get("guessed_position").asInt());
            guessedCode.add(node.get("guess_for_third_clue").get("guessed_position").asInt());
        } catch (Exception e) {
            log.error("intercept_parse_answer_error", e);
            throw new RuntimeException(e);
        }

        return guessedCode;
    }

    private String createPrompt(Player player, List<String> clues, int roundNumber) {
        String finalPrompt = this.fileReaderUtil.readTextFile("decrypto/prompt_interceptor");

        finalPrompt = this.replaceClues(finalPrompt, clues);
        finalPrompt = this.replaceClueHistory(finalPrompt, player, roundNumber);

        return finalPrompt;
    }

    private String replaceClueHistory(String prompt, Player player, int roundNumber) {
        Team team = this.gameState.getTeamOfPlayer(player);
        Team otherTeam = this.gameState.getOtherTeam(team);

        List<String> pastClues1 = this.getPastCluesForCodeDigit(1, otherTeam, roundNumber);
        List<String> pastClues2 = this.getPastCluesForCodeDigit(2, otherTeam, roundNumber);
        List<String> pastClues3 = this.getPastCluesForCodeDigit(3, otherTeam, roundNumber);
        List<String> pastClues4 = this.getPastCluesForCodeDigit(4, otherTeam, roundNumber);

        prompt = prompt.replace("{PAST_CLUES_1}", createPastCluesText(pastClues1));
        prompt = prompt.replace("{PAST_CLUES_2}", createPastCluesText(pastClues2));
        prompt = prompt.replace("{PAST_CLUES_3}", createPastCluesText(pastClues3));
        prompt = prompt.replace("{PAST_CLUES_4}", createPastCluesText(pastClues4));

        return prompt;
    }

    private String createPastCluesText(List<String> pastClues) {
        if (pastClues.isEmpty()) {
            return "No clues are known yet for this secret word.";
        }

        return pastClues.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "));
    }

}
