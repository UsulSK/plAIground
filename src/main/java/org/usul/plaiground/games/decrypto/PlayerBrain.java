package org.usul.plaiground.games.decrypto;

import com.google.inject.Inject;
import org.usul.plaiground.games.decrypto.entities.*;
import org.usul.plaiground.outbound.llm.KoboldLlmConnector;
import org.usul.plaiground.utils.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerBrain {

    private GameWorld gameWorld;

    @Inject
    private FileReader fileReader;

    @Inject
    KoboldLlmConnector llm;

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public List<String> encrypt(Player player, List<Integer> code, int roundNumber) {
        String prompt_general = this.fileReader.readTextFile("decrypto/prompt_general");
        String prompt_encrypt = this.fileReader.readTextFile("decrypto/prompt_encryptor");
        String final_prompt = prompt_general + "\n" + prompt_encrypt;

        final_prompt = final_prompt.replace("{team_setup}", this.getTeamSetupLlmSerialization());
        final_prompt = final_prompt.replace("{game_history}", this.getGameHistoryLlmSerialization(player, roundNumber));
        final_prompt = final_prompt.replace("{secret_words}", this.getSecretWordsLlmSerialization(player));
        final_prompt = final_prompt.replace("{code}", this.getCodeText(code));

        String answer = llm.chat(final_prompt);

        return new ArrayList<>(List.of("apple", "banana", "cherry"));
    }

    private String getSecretWordsLlmSerialization(Player player) {
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
        teamroundText.append(getTeamText(player, otherTeam));
        teamroundText.append(" guessed ");
        teamroundText.append(teamRound.getGuessedCodeByOtherTeam().stream().map(String::valueOf).reduce("", String::concat));
        teamroundText.append(" for the other teams code.\n");
        teamroundText.append(getTeamText(player, team));
        teamroundText.append(" guessed ");
        teamroundText.append(teamRound.getGuessedCodeByOwnTeam().stream().map(String::valueOf).reduce("", String::concat));
        teamroundText.append(" for own teams code.\n");

        return teamroundText.toString();
    }

    private String getCodeText(List<Integer> code) {
        return code.stream().map(String::valueOf).reduce("", String::concat);
    }

    private String getCluesText(List<String> clues) {
        return clues.stream().map(s -> "[" + s + "]").reduce((a, b) -> a + " " + b).orElse("");
    }

    private String getTeamText(Player player, Team team) {
        String teamText = "team " + team.getName() + " (";
        if (this.gameWorld.getTeamOfPlayer(player) == team) {
            teamText = teamText + "your team)";
        } else {
            teamText = teamText + "opponent team)";
        }

        return teamText;
    }

    private String getTeamSetupLlmSerialization() {
        String teamSetup = this.getTeamSetupOfOneTeamLlmSerialization(this.gameWorld.getTeam1()) + "\n";
        teamSetup = teamSetup + this.getTeamSetupOfOneTeamLlmSerialization(this.gameWorld.getTeam2());

        return teamSetup;
    }

    private String getTeamSetupOfOneTeamLlmSerialization(Team team) {
        String teamSetup = "Team " + team.getName() + " has players: ";

        List<String> playerNames = team.getPlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        teamSetup += String.join(", ", playerNames);

        return teamSetup;
    }

    public List<Integer> intercept(Team interceptingTeam, List<String> encryptedCode, int roundNumber) {
        return new ArrayList<>(List.of(4, 3, 2));
    }

    public List<Integer> decrypt(Player player, List<String> encryptedCode, int roundNumber) {
        return new ArrayList<>(List.of(2, 1, 3));
    }
}
