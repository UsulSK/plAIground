package org.usul.plaiground.utils;

import org.json.JSONObject;
import org.usul.plaiground.backend.games.decrypto.entities.GameState;
import org.usul.plaiground.backend.games.decrypto.entities.Player;
import org.usul.plaiground.backend.games.decrypto.llmroles.LlmPromptCreator;
import org.usul.plaiground.backend.outbound.llm.request.RequestBodyBuilderUtil;

import java.util.List;

public class LlmRequestBodyBuilder {

    /*
     * This can be used to generate the requests for the LLM for you to copy to a HTTP client in order to test against a LLM.
     */
    public static void main(String[] args) throws Exception {
        String generateFor = "encrypt"; // just change by hand to encrypt or decrypt or intercept

        FileReaderUtil fileReaderUtil = new FileReaderUtil();

        String promptTemplateGeneral = fileReaderUtil.readTextFile("decrypto/prompt_general");

        GameState gameState = new GameState();

        gameState.getTeam1().getPlayers().add(new Player("player1"));
        gameState.getTeam1().getPlayers().add(new Player("player2"));
        gameState.getTeam2().getPlayers().add(new Player("player3"));
        gameState.getTeam2().getPlayers().add(new Player("player4"));

        String bodyText = "";

        if (generateFor.equals("encrypt")) {
            bodyText = generateForDecrypt(promptTemplateGeneral, gameState);
        }

        JSONObject bodyJson = RequestBodyBuilderUtil.buildJsonBody(bodyText, 400, 0.7F, 123);

        System.out.println("\n\nPrompt:\n\n" + bodyJson.toString(4) + "\n\n");
    }


    private static String generateForDecrypt(String promptTemplateGeneral, GameState gameState) {
        FileReaderUtil fileReaderUtil = new FileReaderUtil();
        String promptTemplateDecrypt = fileReaderUtil.readTextFile("decrypto/prompt_decryptor");

        Player player = gameState.getTeam1().getPlayers().getFirst();

        List<String> clues = List.of("clue1", "clue2", "clue3");
        int roundNumber = 1;

        String finalPrompt = LlmPromptCreator.createPromptForDecrypt(gameState, promptTemplateGeneral,
                promptTemplateDecrypt, player, clues, roundNumber);

        return finalPrompt;
    }

}
