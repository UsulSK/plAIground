package org.usul.plaiground.games.decrypto;

import com.google.inject.Inject;
import org.usul.plaiground.games.decrypto.entities.*;
import org.usul.plaiground.games.decrypto.llmroles.DecryptorLlm;
import org.usul.plaiground.games.decrypto.llmroles.EncryptorLlm;
import org.usul.plaiground.games.decrypto.llmroles.InterceptorLlm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameMaster {
    private final static int MAX_ROUNDS = 8;

    private int currRound = 0;

    private GameWorld gameWorld = null;

    @Inject
    EncryptorLlm brainEncryptor;

    @Inject
    DecryptorLlm brainDecryptor;

    @Inject
    InterceptorLlm brainInterceptor;

    public void playGame() {
        GameLog gameLog = this.gameWorld.getGameLog();
        for (this.currRound = 0; this.currRound < MAX_ROUNDS; this.currRound++) {
            this.gameWorld.getGameLog().addRound(this.gameWorld.getTeam1(), this.gameWorld.getTeam2());
            this.playRound();

            if ((gameLog.getRounds().getLast().getTeam1InterceptTokens() >= 2) || (gameLog.getRounds().getLast().getTeam2InterceptTokens() >= 2)
                    || (gameLog.getRounds().getLast().getTeam1MissCommTokens() >= 2) || (gameLog.getRounds().getLast().getTeam2MissCommTokens() >= 2)) {
                break;
            }
        }

        this.determineWinningTeam();
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.brainEncryptor.setGameWorld(gameWorld);
        this.brainDecryptor.setGameWorld(gameWorld);
        this.brainInterceptor.setGameWorld(gameWorld);
    }

    private void determineWinningTeam() {
        Round lastRound = this.gameWorld.getGameLog().getRounds().getLast();
        int pointsTeam1 = lastRound.getTeam1InterceptTokens() - lastRound.getTeam1MissCommTokens();
        int pointsTeam2 = lastRound.getTeam2InterceptTokens() - lastRound.getTeam2MissCommTokens();

        if (pointsTeam1 == pointsTeam2) {
            return;
        } else if (pointsTeam1 > pointsTeam2) {
            this.gameWorld.setWinningTeam(this.gameWorld.getTeam1());
        } else {
            this.gameWorld.setWinningTeam(this.gameWorld.getTeam2());
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

        List<String> encryptedCodes = brainEncryptor.encrypt(currEncryptor, code, this.currRound);
        teamRound.getEncryptedCode().addAll(encryptedCodes);

        if( round.getRoundNumber() > 0 ) {
            List<Integer> guessedCodes = brainInterceptor.intercept(interceptingTeam, encryptedCodes, this.currRound);
            teamRound.getGuessedCodeByOtherTeam().addAll(guessedCodes);

            if (guessedCodes.equals(code)) {
                round.addInterceptionTokensToken(interceptingTeam);
            }
        }

        List<Integer> decryptedCodes = brainDecryptor.decrypt(currDecryptor, encryptedCodes, this.currRound);
        teamRound.getGuessedCodeByOwnTeam().addAll(decryptedCodes);

        if (!decryptedCodes.equals(code)) {
            round.addMiscommunicationToken(transmittingTeam);
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
