package org.usul.plaiground.backend.games.decrypto.llmroles;

import com.google.inject.Inject;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usul.plaiground.backend.games.decrypto.entities.*;
import org.usul.plaiground.backend.outbound.llm.KoboldLlmConnector;
import org.usul.plaiground.utils.FileReaderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DecryptoLlmParent {

    protected static final Logger log = LoggerFactory.getLogger(DecryptoLlmParent.class);

    @Setter
    protected GameState gameState;

    @Inject
    private KoboldLlmConnector llm;

    @Inject
    protected FileReaderUtil fileReaderUtil;

    protected <T> List<T> useLlm(String prompt, Function<String, List<T>> parser) {
        int maxRetries = 5;

        String answer = null;
        int currTry = 0;
        boolean parsingSucceeded = true;
        List<T> parsedAnswer = new ArrayList<>();
        do {
            parsingSucceeded = true;
            if (currTry >= maxRetries) {
                break;
            }

            try {
                answer = this.llm.chat(prompt, 350, 0.7F, 0.9F);
                parsedAnswer = parser.apply(answer);
            } catch (Exception e) {
                parsingSucceeded = false;
                currTry++;
                log.error("llm_parsing_error!", e);
            }
        } while (!parsingSucceeded);

        if ((currTry >= maxRetries) && parsedAnswer.isEmpty()) {
            throw new RuntimeException("llm_too_many_retries");
        }

        return parsedAnswer;
    }

    protected String replaceReferringSecretWords(String prompt, Player player, List<Integer> code) {
        List<String> secretWords = this.gameState.getTeamOfPlayer(player).getKeywords();
        prompt = prompt.replace("{REFERRING_SECRET_1}", secretWords.get(code.get(0) - 1));
        prompt = prompt.replace("{REFERRING_SECRET_2}", secretWords.get(code.get(1) - 1));
        prompt = prompt.replace("{REFERRING_SECRET_3}", secretWords.get(code.get(2) - 1));

        return prompt;
    }

    protected String replaceSecretWords(String prompt, Player player) {
        List<String> secretWords = this.gameState.getTeamOfPlayer(player).getKeywords();
        prompt = prompt.replace("{SECRET_WORD_1}", secretWords.get(0));
        prompt = prompt.replace("{SECRET_WORD_2}", secretWords.get(1));
        prompt = prompt.replace("{SECRET_WORD_3}", secretWords.get(2));
        prompt = prompt.replace("{SECRET_WORD_4}", secretWords.get(3));

        return prompt;
    }

    protected String replaceCode(String prompt, List<Integer> code) {
        prompt = prompt.replace("{SECRET_CODE}", getCodeText(code));
        prompt = prompt.replace("{SECRET_CODE_1}", code.get(0).toString());
        prompt = prompt.replace("{SECRET_CODE_2}", code.get(1).toString());
        prompt = prompt.replace("{SECRET_CODE_3}", code.get(2).toString());

        return prompt;
    }

    protected static String getCodeText(List<Integer> code) {
        return code.stream().map(String::valueOf).reduce("", String::concat);
    }

    protected List<String> getPastCluesForCodeDigit(int codeDigit, Team team) {
        return this.getPastCluesForCodeDigit(codeDigit, team, -1);
    }

    protected List<String> getPastCluesForCodeDigit(int codeDigit, Team team, int endRoundNumberExcluding) {
        List<String> pastCluesForCodeDigit = new ArrayList<>();

        for (Round round : this.gameState.getGameLog().getRounds()) {
            if ((endRoundNumberExcluding >= 0) && (round.getRoundNumber() == endRoundNumberExcluding)) {
                break;
            }

            TeamRound teamRound = round.getTeamInfo().get(team.getName());
            if (!teamRound.getCode().contains(codeDigit)) {
                continue;
            }
            if (teamRound.getEncryptedCode().isEmpty()) {
                continue;
            }

            int codeDigitPosition = teamRound.getCode().indexOf(codeDigit);

            String pastClue = teamRound.getEncryptedCode().get(codeDigitPosition);
            pastCluesForCodeDigit.add(pastClue);
        }

        return pastCluesForCodeDigit;
    }

    protected String replaceClues(String prompt, List<String> clues) {
        prompt = prompt.replace("{CLUE_1}", clues.get(0));
        prompt = prompt.replace("{CLUE_2}", clues.get(1));
        prompt = prompt.replace("{CLUE_3}", clues.get(2));

        return prompt;
    }
}
