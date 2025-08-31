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

    protected <T extends ILlmResponse> T useLlm(String prompt, Function<String, T> parser) {
        int maxRetries = 5;
        int currTry = 0;

        while (currTry < maxRetries) {
            try {
                String answer = this.llm.chat(prompt, 350, 0.7F, 0.9F);
                return parser.apply(answer);
            } catch (Exception e) {
                currTry++;
                log.error("llm_parsing_error! try=" + currTry, e);
            }
        }

        throw new RuntimeException("llm_too_many_retries");
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

    protected String replaceClues(String prompt, List<String> clues) {
        prompt = prompt.replace("{CLUE_1}", clues.get(0));
        prompt = prompt.replace("{CLUE_2}", clues.get(1));
        prompt = prompt.replace("{CLUE_3}", clues.get(2));

        return prompt;
    }
}
