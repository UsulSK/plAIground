package org.usul.plaiground.backend.games.decrypto.llmroles;

import org.usul.plaiground.backend.games.decrypto.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LlmPromptCreator {
    public static String createPromptForEncrypt(GameState gameState, String promptTemplateGeneral, String promptTemplateEncrypt,
                                                Player player, List<Integer> code, int roundNumber) {
        String finalPrompt = promptTemplateGeneral + "\n" + promptTemplateEncrypt;

        finalPrompt = getPromptForGeneralTemplate(gameState, finalPrompt, player, roundNumber);
        finalPrompt = finalPrompt.replace("{secret_words}", getSecretWordsLlmSerialization(gameState, player));
        finalPrompt = finalPrompt.replace("{code}", getCodeText(code));
        finalPrompt = finalPrompt.replace("{used_clues}", getAllUsedClues(gameState, player));

        return finalPrompt;
    }

    public static String createPromptForIntercept(GameState gameState, String promptTemplateGeneral, String promptTemplateIntercept,
                                                  Player player, List<String> clues, int roundNumber) {
        String finalPrompt = promptTemplateGeneral + "\n" + promptTemplateIntercept;
        finalPrompt = getPromptForGeneralTemplate(gameState, finalPrompt, player, roundNumber);
        finalPrompt = finalPrompt.replace("{clues}", getCluesLlmSerialization(clues));

        return finalPrompt;
    }

    public static String createPromptForDecrypt(GameState gameState, String promptTemplateGeneral, String promptTemplateDecrypt,
                                                Player player, List<String> clues, int roundNumber) {
        String finalPrompt = promptTemplateGeneral + "\n" + promptTemplateDecrypt;
        finalPrompt = getPromptForGeneralTemplate(gameState, finalPrompt, player, roundNumber);
        finalPrompt = finalPrompt.replace("{secret_words}", getSecretWordsLlmSerialization(gameState, player));
        finalPrompt = finalPrompt.replace("{clues}", getCluesLlmSerialization(clues));

        return finalPrompt;
    }

    private static String getAllUsedClues(GameState gameState, Player player) {
        Team teamOfPlayer = gameState.getTeamOfPlayer(player);
        List<String> usedCodesForDigit1 = new ArrayList<>();
        List<String> usedCodesForDigit2 = new ArrayList<>();
        List<String> usedCodesForDigit3 = new ArrayList<>();
        for (Round round : gameState.getGameLog().getRounds()) {
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

        if (usedCodesForDigit1.isEmpty()) {
            return "no clues have been given yet";
        }

        return "for first digit: " + usedCodesForDigit1.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).get()
                + "; for second digit: " + usedCodesForDigit2.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).get()
                + "; for third digit: " + usedCodesForDigit3.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).get();
    }

    private static String getCluesLlmSerialization(List<String> clues) {
        String result = clues.stream()
                .map(s -> "[" + s + "]")
                .collect(Collectors.joining(" "));
        return result;
    }

    private static String getSecretWordsLlmSerialization(GameState gameState, Player player) {
        List<String> secretWords = gameState.getTeamOfPlayer(player).getKeywords();
        StringBuilder swSb = new StringBuilder();

        int counter = 1;
        for (String secretWord : secretWords) {
            swSb.append(counter);
            swSb.append(": ");
            swSb.append(secretWord);
            swSb.append("\n");
            counter++;
        }

        return swSb.toString();
    }

    private static String getPromptForGeneralTemplate(GameState gameState, String promptTemplateGeneral, Player player, int roundNumber) {
        promptTemplateGeneral = promptTemplateGeneral.replace("{game_history}", getGameHistoryLlmSerialization(gameState, player, roundNumber));

        return promptTemplateGeneral;
    }

    private static String getGameHistoryLlmSerialization(GameState gameState, Player player, int roundNumber) {
        StringBuilder gameHistory = new StringBuilder("\n");

        for (Round round : gameState.getGameLog().getRounds()) {
            gameHistory.append("Round ");
            gameHistory.append(round.getRoundNumber() + 1);
            if (round.getRoundNumber() == roundNumber) {
                gameHistory.append(" (current round)");
            }
            gameHistory.append(":\n\n");

            String team1Log = getTeamRoundLlmSerialization(gameState, player, gameState.getTeam1(), round);
            String team2Log = getTeamRoundLlmSerialization(gameState, player, gameState.getTeam2(), round);

            if (team1Log.isEmpty() && team2Log.isEmpty()) {
                gameHistory.append("-");
            } else if (round.getRoundNumber() == 0) {
                gameHistory.append("In the first round there is no intercepting the other team.\n\n");
            }

            if (!team1Log.isEmpty()) {
                gameHistory.append(team1Log);
                gameHistory.append("\n");
            }
            if (!team2Log.isEmpty()) {
                gameHistory.append(team2Log);
            }

            gameHistory.append("\n");
        }

        return gameHistory.toString();
    }

    private static String getTeamRoundLlmSerialization(GameState gameState, Player player, Team team, Round round) {
        TeamRound teamRound = round.getTeamInfo().get(team.getName());
        Team otherTeam = gameState.getOtherTeam(team);

        if (teamRound == null) {
            return "";
        }

        if (teamRound.getGuessedCodeByOwnTeam().isEmpty()) {
            return "";
        }

        StringBuilder teamroundText = new StringBuilder();

        teamroundText.append(getTeamText(gameState, player, team));
        teamroundText.append(" had code ");
        teamroundText.append(getCodeText(teamRound.getCode()));
        teamroundText.append(" and gave clues ");
        teamroundText.append(getCluesText(teamRound.getEncryptedCode()));
        teamroundText.append(".\n");

        if (round.getRoundNumber() > 0) {
            teamroundText.append(getTeamText(gameState, player, otherTeam));
            teamroundText.append(" guessed ");
            teamroundText.append(teamRound.getGuessedCodeByOtherTeam().stream().map(String::valueOf).reduce("", String::concat));
            teamroundText.append(" for the other teams code.");
            if (teamRound.getCode().equals(teamRound.getGuessedCodeByOtherTeam()) && (gameState.getTeamOfPlayer(player) == team)) {
                teamroundText.append(" This guess was CORRECT! The clues here have been maybe too obvious.");
            }
            teamroundText.append("\n");
        }

        teamroundText.append(getTeamText(gameState, player, team));
        teamroundText.append(" guessed ");
        teamroundText.append(teamRound.getGuessedCodeByOwnTeam().stream().map(String::valueOf).reduce("", String::concat));
        teamroundText.append(" for own teams code.");
        if (!teamRound.getCode().equals(teamRound.getGuessedCodeByOwnTeam())) {
            teamroundText.append(" This guess was NOT correct! The clues here may have been too obscure.");
        }
        teamroundText.append("\n");

        return teamroundText.toString();
    }

    private static String getTeamText(GameState gameState, Player player, Team team) {
        String teamText = "";

        if (gameState.getTeamOfPlayer(player) == team) {
            teamText = teamText + "Your team";
        } else {
            teamText = teamText + "Oponent team";
        }

        return teamText;
    }

    private static String getCodeText(List<Integer> code) {
        return code.stream().map(String::valueOf).reduce("", String::concat);
    }

    private static String getCluesText(List<String> clues) {
        return clues.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).orElse("");
    }
}
