package org.usul.plaiground.utils;

import org.json.JSONObject;
import org.usul.plaiground.backend.games.decrypto.entities.*;
import org.usul.plaiground.backend.games.decrypto.llmroles.LlmPromptCreator;
import org.usul.plaiground.backend.outbound.llm.request.RequestBodyBuilderUtil;

import java.util.List;

public class LlmRequestBodyBuilder {

    /*
     * This can be used to generate the requests for the LLM for you to copy to a HTTP client in order to test against a LLM.
     */
    public static void main(String[] args) throws Exception {
        String generateFor = "intercept"; // just change by hand to encrypt or decrypt or intercept

        FileReaderUtil fileReaderUtil = new FileReaderUtil();

        String promptTemplateGeneral = fileReaderUtil.readTextFile("decrypto/prompt_general");

        GameState gameState = createGameState(generateFor);

        gameState.getTeam1().getPlayers().add(new Player("player1"));
        gameState.getTeam1().getPlayers().add(new Player("player2"));
        gameState.getTeam2().getPlayers().add(new Player("player3"));
        gameState.getTeam2().getPlayers().add(new Player("player4"));

        String bodyText = "";

        if (generateFor.equals("decrypt")) {
            bodyText = generateForDecrypt(promptTemplateGeneral, gameState);
        } else if (generateFor.equals("intercept")) {
            bodyText = generateForIntercept(promptTemplateGeneral, gameState);
        } else if (generateFor.equals("encrypt")) {
            bodyText = generateForEncrypt(promptTemplateGeneral, gameState);
        }

        System.out.println(generateFor);
        System.out.println("\n\nReadable prompt:\n\n" + bodyText + "\n\n");

        JSONObject bodyJson = RequestBodyBuilderUtil.buildJsonBody(bodyText, 400, 0.7F, 123);

        System.out.println("\n\nPrompt for request:\n\n" + bodyJson.toString(4) + "\n\n");
    }

    private static String generateForEncrypt(String promptTemplateGeneral, GameState gameState) {
        FileReaderUtil fileReaderUtil = new FileReaderUtil();
        String promptTemplateEncrypt = fileReaderUtil.readTextFile("decrypto/prompt_encryptor");

        Player player = gameState.getTeam1().getPlayers().getFirst();

        List<Integer> code = List.of(2, 1, 4);
        int roundNumber = 2;

        String finalPrompt = LlmPromptCreator.createPromptForEncrypt(gameState, promptTemplateGeneral,
                promptTemplateEncrypt, player, code, roundNumber);

        return finalPrompt;
    }

    private static String generateForIntercept(String promptTemplateGeneral, GameState gameState) {
        FileReaderUtil fileReaderUtil = new FileReaderUtil();
        String promptTemplateIntercept = fileReaderUtil.readTextFile("decrypto/prompt_interceptor");

        Player player = gameState.getTeam1().getPlayers().getFirst();

        List<String> clues = List.of("clue1", "clue2", "clue3");
        int roundNumber = 2;

        String finalPrompt = LlmPromptCreator.createPromptForIntercept(gameState, promptTemplateGeneral,
                promptTemplateIntercept, player, clues, roundNumber);

        return finalPrompt;
    }

    private static String generateForDecrypt(String promptTemplateGeneral, GameState gameState) {
        FileReaderUtil fileReaderUtil = new FileReaderUtil();
        String promptTemplateDecrypt = fileReaderUtil.readTextFile("decrypto/prompt_decryptor");

        Player player = gameState.getTeam1().getPlayers().getFirst();

        List<String> clues = List.of("clue1", "clue2", "clue3");
        int roundNumber = 2;

        String finalPrompt = LlmPromptCreator.createPromptForDecrypt(gameState, promptTemplateGeneral,
                promptTemplateDecrypt, player, clues, roundNumber);

        return finalPrompt;
    }

    private static GameState createGameState(String generateFor) {
        GameState gameState = new GameState();
        Team team1 = gameState.getTeam1();
        Team team2 = gameState.getTeam2();

        team1.getPlayers().add(new Player("player1"));
        team1.getPlayers().add(new Player("player2"));
        team2.getPlayers().add(new Player("player3"));
        team2.getPlayers().add(new Player("player4"));
        team1.setKeywords(List.of("keywordTeam1_1", "keywordTeam1_2", "keywordTeam1_3", "keywordTeam1_4"));
        team2.setKeywords(List.of("keywordTeam2_1", "keywordTeam2_2", "keywordTeam2_3", "keywordTeam2_4"));

        GameLog gameLog = gameState.getGameLog();

        gameLog.addRound(team1, team2);
        Round round1 = gameLog.getRounds().getLast();
        round1.setStartingTeam(gameState.getTeam1());
        round1.addMiscommunicationToken(team1);
        round1.addInterceptionTokensToken(team2);
        TeamRound teamRound1_1 = new TeamRound();
        TeamRound teamRound2_1 = new TeamRound();
        round1.setRoundNumber(0);
        round1.getTeamInfo().put(team1.getName(), teamRound1_1);
        round1.getTeamInfo().put(team2.getName(), teamRound2_1);
        teamRound1_1.setCode(List.of(1, 2, 3));
        teamRound1_1.setEncryptedCode(List.of("clue_team1_round1_1", "clue_team1_round1_2", "clue_team1_round1_3"));
        teamRound1_1.setGuessedCodeByOwnTeam(List.of(4, 2, 3));
        teamRound1_1.setGuessedCodeByOtherTeam(List.of(1, 2, 3));
        teamRound2_1.setCode(List.of(1, 4, 2));
        teamRound2_1.setEncryptedCode(List.of("clue_team2_round1_1", "clue_team2_round1_2", "clue_team2_round1_3"));
        teamRound2_1.setGuessedCodeByOwnTeam(List.of(1, 4, 2));
        teamRound2_1.setGuessedCodeByOtherTeam(List.of(4, 2, 3));

        gameLog.addRound(team1, team2);
        Round round2 = gameLog.getRounds().getLast();
        round2.setStartingTeam(gameState.getTeam2());
        TeamRound teamRound1_2 = new TeamRound();
        TeamRound teamRound2_2 = new TeamRound();
        round2.setRoundNumber(1);
        round2.getTeamInfo().put(team1.getName(), teamRound1_2);
        round2.getTeamInfo().put(team2.getName(), teamRound2_2);
        teamRound1_2.setCode(List.of(2, 1, 3));
        teamRound1_2.setEncryptedCode(List.of("clue_team1_round2_1", "clue_team1_round2_2", "clue_team1_round2_3"));
        teamRound1_2.setGuessedCodeByOwnTeam(List.of(2, 1, 3));
        teamRound1_2.setGuessedCodeByOtherTeam(List.of(1, 4, 3));
        teamRound2_2.setCode(List.of(4, 1, 2));
        teamRound2_2.setEncryptedCode(List.of("clue_team2_round2_1", "clue_team2_round2_2", "clue_team2_round2_3"));
        teamRound2_2.setGuessedCodeByOwnTeam(List.of(4, 1, 2));
        teamRound2_2.setGuessedCodeByOtherTeam(List.of(4, 2, 1));

        gameLog.addRound(team1, team2);
        Round round3 = gameLog.getRounds().getLast();
        round3.setRoundNumber(2);
        round3.setStartingTeam(gameState.getTeam1());
        TeamRound teamRound1_3 = new TeamRound();
        round3.getTeamInfo().put(team1.getName(), teamRound1_3);
        teamRound1_3.setCode(List.of(1, 4, 3));

        if (generateFor.equals("intercept") || generateFor.equals("decrypt")) {
            teamRound1_3.setEncryptedCode(List.of("clue_team1_round3_1", "clue_team1_round3_2", "clue_team1_round3_3"));
        }
        if (generateFor.equals("decrypt")) {
            teamRound1_3.setGuessedCodeByOtherTeam(List.of(2, 4, 3));
        }

        return gameState;
    }

}
