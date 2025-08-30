package org.usul.plaiground.backend.games.decrypto;

import com.google.inject.Inject;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usul.plaiground.backend.games.decrypto.entities.*;
import org.usul.plaiground.backend.games.decrypto.llmroles.DecryptorLlm;
import org.usul.plaiground.backend.games.decrypto.llmroles.EncryptorLlm;
import org.usul.plaiground.backend.games.decrypto.llmroles.InterceptorLlm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecryptoGameLogic {

    private static final Logger log = LoggerFactory.getLogger(DecryptoGameLogic.class);
    private final static int MAX_ROUNDS = 8;

    private Runnable gameUpdateListener;

    @Getter
    private final GameState gameState = new GameState();

    @Inject
    EncryptorLlm brainEncryptor;

    @Inject
    DecryptorLlm brainDecryptor;

    @Inject
    InterceptorLlm brainInterceptor;

    public void playGame(Runnable gameUpdateListener) {
        this.brainEncryptor.setGameState(this.gameState);
        this.brainDecryptor.setGameState(this.gameState);
        this.brainInterceptor.setGameState(this.gameState);
        this.gameUpdateListener = gameUpdateListener;
        log.info("created_game \n" + this.gameState + "\n");

        GameLog gameLog = this.gameState.getGameLog();
        while (this.gameState.getRoundNumber() < MAX_ROUNDS) {
            this.gameState.getGameLog().addRound(this.gameState.getTeam1(), this.gameState.getTeam2());

            log.info("ROUND " + (this.gameState.getRoundNumber() + 1) + "\n");

            this.playRound();

            if ((gameLog.getRounds().getLast().getTeam1InterceptTokens() >= 2) || (gameLog.getRounds().getLast().getTeam2InterceptTokens() >= 2)
                    || (gameLog.getRounds().getLast().getTeam1MissCommTokens() >= 2) || (gameLog.getRounds().getLast().getTeam2MissCommTokens() >= 2)) {
                break;
            }
        }

        this.determineWinningTeam();
        this.gameState.setGameFinished(true);
        this.gameUpdateListener.run();
    }

    public void reset() {
        this.gameState.reset();
    }

    private void determineWinningTeam() {
        Round lastRound = this.gameState.getGameLog().getRounds().getLast();
        int pointsTeam1 = this.gameState.getTotalPointsForTeam(this.gameState.getTeam1());
        int pointsTeam2 = this.gameState.getTotalPointsForTeam(this.gameState.getTeam2());

        if (pointsTeam1 == pointsTeam2) {
            return;
        } else if (pointsTeam1 > pointsTeam2) {
            this.gameState.setWinningTeam(this.gameState.getTeam1());
        } else {
            this.gameState.setWinningTeam(this.gameState.getTeam2());
        }
    }

    private void playRound() {
        Team startingTeam = getStartingTeam();
        Team secondTeam = this.gameState.getOtherTeam(startingTeam);

        Round round = this.gameState.getGameLog().getRounds().getLast();
        round.setStartingTeam(startingTeam);

        playRoundForTeam(startingTeam, secondTeam);
        this.gameUpdateListener.run();
        playRoundForTeam(secondTeam, startingTeam);
        this.gameUpdateListener.run();
    }

    private Team getStartingTeam() {
        if (this.gameState.getRoundNumber() % 2 == 0) {
            return this.gameState.getTeam1();
        }
        return this.gameState.getTeam2();
    }

    private void playRoundForTeam(Team transmittingTeam, Team interceptingTeam) {
        Player currEncryptor = this.getCurrentEncryptor(transmittingTeam);
        Player currDecryptor = transmittingTeam.getOtherPlayer(currEncryptor);
        Player currInterceptor = interceptingTeam.getPlayers().getFirst();

        Round round = this.gameState.getGameLog().getRounds().getLast();
        TeamRound teamRound = new TeamRound();
        round.setRoundNumber(this.gameState.getRoundNumber());
        round.getTeamInfo().put(transmittingTeam.getName(), teamRound);

        List<Integer> code = this.generateCode();
        teamRound.getCode().addAll(code);

        log.info(transmittingTeam.getName() + " do_encrypt_secret_words " + transmittingTeam.getKeywords() + " and code " + code);
        List<String> encryptedCodes = brainEncryptor.encrypt(currEncryptor, code, this.gameState.getRoundNumber());
        log.info("encrypted_secret_words: " + encryptedCodes + "\n");
        teamRound.getEncryptedCode().addAll(encryptedCodes);
        this.gameUpdateListener.run();

        boolean interceptingSuccessful = false;
        if (round.getRoundNumber() > 0) {
            log.info(interceptingTeam.getName() + " INTERCEPT");
            List<Integer> guessedCodes = brainInterceptor.intercept(currInterceptor, encryptedCodes, this.gameState.getRoundNumber());
            log.info("intercept_guess: " + guessedCodes + "\n");
            teamRound.getGuessedCodeByOtherTeam().addAll(guessedCodes);

            if (guessedCodes.equals(code)) {
                interceptingSuccessful = true;
                round.addInterceptionTokensToken(interceptingTeam);
                log.info("INTERCEPTION_SUCCEEDED FOR TEAM " + interceptingTeam.getName() + "! \n");
            }
        }
        this.gameUpdateListener.run();

        if (!interceptingSuccessful) {
            log.info(transmittingTeam.getName() + " DECRYPT");
            List<Integer> decryptedCodes = brainDecryptor.decrypt(currDecryptor, encryptedCodes, this.gameState.getRoundNumber());
            log.info("guesser_guess: " + decryptedCodes + "\n");
            teamRound.getGuessedCodeByOwnTeam().addAll(decryptedCodes);

            if (!decryptedCodes.equals(code)) {
                round.addMiscommunicationToken(transmittingTeam);
                log.info("TEAM_GUESSED_OWN_CODE_WRONG: " + transmittingTeam.getName() + "! \n");
            }
        }
    }

    private Player getCurrentEncryptor(Team team) {
        if (this.gameState.getRoundNumber() % 2 == 0) {
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
