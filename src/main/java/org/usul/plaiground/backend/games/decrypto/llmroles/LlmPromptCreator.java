package org.usul.plaiground.backend.games.decrypto.llmroles;

import org.usul.plaiground.backend.games.decrypto.entities.*;

import java.util.List;
import java.util.stream.Collectors;

public class LlmPromptCreator {

    public static String createPromptForIntercept(GameState gameState, String promptTemplateGeneral, String promptTemplateIntercept,
                                                  Player player, List<String> clues, int roundNumber) {
        String finalPrompt = promptTemplateGeneral + "\n" + promptTemplateIntercept;
        finalPrompt = getPromptForGeneralTemplate(gameState, finalPrompt, player, roundNumber);
        finalPrompt = finalPrompt.replace("{clues}", getCluesLlmSerialization(clues));

        return finalPrompt;
    }

    private static String getCluesLlmSerialization(List<String> clues) {
        String result = clues.stream()
                .map(s -> "[" + s + "]")
                .collect(Collectors.joining(" "));
        return result;
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
