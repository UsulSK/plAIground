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
import java.util.stream.Collectors;

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
                answer = this.llm.chat(prompt);
                parsedAnswer = parser.apply(answer);
            } catch (Exception e) {
                parsingSucceeded = false;
                currTry++;
                log.info("Error with LLM! Trying again!");
            }
        } while (!parsingSucceeded);

        if ((currTry >= maxRetries) && parsedAnswer.isEmpty()) {
            throw new RuntimeException("Gave up using the LLM!");
        }

        return parsedAnswer;
    }
}
