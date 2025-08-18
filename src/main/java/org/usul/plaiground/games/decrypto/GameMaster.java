package org.usul.plaiground.games.decrypto;

import com.google.inject.Inject;
import org.usul.plaiground.games.decrypto.entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameMaster {
    private final static int MAX_ROUNDS = 8;

    private int currRound = 0;

    private GameWorld gameWorld = null;

    @Inject
    PlayerBrain brain;

    public void playGame() {
        for (this.currRound = 0; this.currRound < MAX_ROUNDS; this.currRound++) {
            this.gameWorld.getGameLog().addRound();
            this.playRound();

            if ((this.gameWorld.getTeam1().getInterceptionTokens() >= 2) || (this.gameWorld.getTeam2().getInterceptionTokens() >= 2)
                    || (this.gameWorld.getTeam1().getMiscommunicationTokens() >= 2) || (this.gameWorld.getTeam2().getMiscommunicationTokens() >= 2)) {
                break;
            }
        }

        this.determineWinningTeam();
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.brain.setGameWorld(gameWorld);
    }

    private void determineWinningTeam() {
        Team team1 = this.gameWorld.getTeam1();
        Team team2 = this.gameWorld.getTeam2();
        int pointsTeam1 = team1.getInterceptionTokens() -team1.getMiscommunicationTokens();
        int pointsTeam2 = team2.getInterceptionTokens() - team2.getMiscommunicationTokens();

        if (pointsTeam1 == pointsTeam2) {
            return;
        } else if (pointsTeam1 > pointsTeam2) {
            this.gameWorld.setWinningTeam(team1);
        } else {
            this.gameWorld.setWinningTeam(team2);
        }
    }

    private void playRound() {
        Team startingTeam = getStartingTeam();
        Team secondTeam = this.gameWorld.getOtherTeam(startingTeam);

        Round round = this.gameWorld.getGameLog().getRounds().get(this.currRound);
        round.setStartingTeam(startingTeam);

        playRoundForTeam(startingTeam, secondTeam);
        playRoundForTeam(secondTeam, startingTeam);
    }

    private Team getStartingTeam() {
        if (this.currRound % 2 == 0) {
            return this.gameWorld.getTeam1();
        }
        return this.gameWorld.getTeam2();
    }

    private void playRoundForTeam(Team transmittingTeam, Team interceptingTeam) {
        Player currEncryptor = this.getCurrentEncryptor(transmittingTeam);
        Player currDecryptor = transmittingTeam.getOtherPlayer(currEncryptor);

        Round round = this.gameWorld.getGameLog().getRounds().get(this.currRound);
        TeamRound teamRound = new TeamRound();
        round.setRoundNumber(this.currRound);
        round.getTeamInfo().put(transmittingTeam.getName(), teamRound);

        teamRound.setDecryptor(currDecryptor);
        teamRound.setEncryptor(currEncryptor);

        List<Integer> code = this.generateCode();
        teamRound.getCode().addAll(code);

        List<String> encryptedCodes = brain.encrypt(currEncryptor, code, this.currRound);
        teamRound.getEncryptedCode().addAll(encryptedCodes);

        List<Integer> guessedCodes = brain.intercept(interceptingTeam, encryptedCodes, this.currRound);
        teamRound.getGuessedCodeByOtherTeam().addAll(guessedCodes);

        if (guessedCodes.equals(code)) {
            interceptingTeam.addInterceptionTokensToken();
        }

        List<Integer> decryptedCodes = brain.decrypt(currDecryptor, encryptedCodes, this.currRound);
        teamRound.getGuessedCodeByOwnTeam().addAll(decryptedCodes);

        if (!decryptedCodes.equals(code)) {
            transmittingTeam.addMiscommunicationToken();
        }
    }

    private Player getCurrentEncryptor(Team team) {
        if (this.currRound % 2 == 0) {
            return team.getPlayers().getFirst();
        }
        return team.getPlayers().getLast();
    }

    private List<Integer> generateCode() {
        List<Integer> digits = new ArrayList<>();
        digits.add(1);
        digits.add(2);
        digits.add(3);
        digits.add(4);

        Collections.shuffle(digits);

        return digits.subList(0, 3);
    }
}
