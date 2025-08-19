package org.usul.plaiground.games.decrypto.llmroles;

import org.usul.plaiground.games.decrypto.entities.*;

import java.util.List;
import java.util.stream.Collectors;

public class DecryptoLlmParent {

    protected GameWorld gameWorld;

    protected String getPromptForGeneralTemplate(String promptTemplateGeneral, Player player, int roundNumber) {
        promptTemplateGeneral = promptTemplateGeneral.replace("{game_history}", this.getGameHistoryLlmSerialization(player, roundNumber));

        return promptTemplateGeneral;
    }

    protected String getSecretWordsLlmSerialization(Player player) {
        List<String> secretWords = this.gameWorld.getTeamOfPlayer(player).getKeywords();
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

    private String getGameHistoryLlmSerialization(Player player, int roundNumber) {
        StringBuilder gameHistory = new StringBuilder("\n");

        for (Round round : this.gameWorld.getGameLog().getRounds()) {
            gameHistory.append("Round ");
            gameHistory.append(round.getRoundNumber() + 1);
            if (round.getRoundNumber() == roundNumber) {
                gameHistory.append(" (current round)");
            }
            gameHistory.append(":\n\n");

            String team1Log = this.getTeamRoundLlmSerialization(player, this.gameWorld.getTeam1(), round);
            String team2Log = this.getTeamRoundLlmSerialization(player, this.gameWorld.getTeam2(), round);

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

    private String getTeamRoundLlmSerialization(Player player, Team team, Round round) {
        TeamRound teamRound = round.getTeamInfo().get(team.getName());
        Team otherTeam = this.gameWorld.getOtherTeam(team);

        if (teamRound == null) {
            return "";
        }

        if (teamRound.getGuessedCodeByOwnTeam().isEmpty()) {
            return "";
        }

        StringBuilder teamroundText = new StringBuilder();

        teamroundText.append(getTeamText(player, team));
        teamroundText.append(" had code ");
        teamroundText.append(getCodeText(teamRound.getCode()));
        teamroundText.append(" and gave clues ");
        teamroundText.append(this.getCluesText(teamRound.getEncryptedCode()));
        teamroundText.append(".\n");

        if (round.getRoundNumber() > 0) {
            teamroundText.append(getTeamText(player, otherTeam));
            teamroundText.append(" guessed ");
            teamroundText.append(teamRound.getGuessedCodeByOtherTeam().stream().map(String::valueOf).reduce("", String::concat));
            teamroundText.append(" for the other teams code.");
            if (teamRound.getCode().equals(teamRound.getGuessedCodeByOtherTeam()) && (this.gameWorld.getTeamOfPlayer(player) == team)) {
                teamroundText.append(" This guess was CORRECT! The clues here have been maybe too obvious.");
            }
            teamroundText.append("\n");
        }

        teamroundText.append(getTeamText(player, team));
        teamroundText.append(" guessed ");
        teamroundText.append(teamRound.getGuessedCodeByOwnTeam().stream().map(String::valueOf).reduce("", String::concat));
        teamroundText.append(" for own teams code.");
        if (!teamRound.getCode().equals(teamRound.getGuessedCodeByOwnTeam())) {
            teamroundText.append(" This guess was NOT correct! The clues here may have been too obscure.");
        }
        teamroundText.append("\n");

        return teamroundText.toString();
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    private String getCluesText(List<String> clues) {
        return clues.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).orElse("");
    }

    private String getTeamText(Player player, Team team) {
        String teamText = "";

        if (this.gameWorld.getTeamOfPlayer(player) == team) {
            teamText = teamText + "Your team";
        } else {
            teamText = teamText + "Oponent team";
        }

        return teamText;
    }

    protected String getCodeText(List<Integer> code) {
        return code.stream().map(String::valueOf).reduce("", String::concat);
    }
}
