package org.usul.plaiground.backend.games.decrypto;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usul.plaiground.backend.games.decrypto.entities.GameState;
import org.usul.plaiground.backend.games.decrypto.entities.Player;
import org.usul.plaiground.utils.FileReaderUtil;
import org.usul.plaiground.utils.RandomizerUtil;

import java.util.*;

public class DecryptoGame {

    private static final Logger log = LoggerFactory.getLogger(DecryptoGame.class);

    public static final String PLAYER_NAMES_FILE_NAME = "decrypto/playernames";
    public static final String NOUNS_FILE_NAME = "decrypto/nouns";

    @Inject
    FileReaderUtil fileReaderUtil;

    @Inject
    DecryptoGameLogic decryptoGameLogic;

    @Inject
    RandomizerUtil randomizerUtil;

    public void startGame() {
        log.info("starting game Decrypto");

        this.reset();
        GameState gameState = this.decryptoGameLogic.getGameState();
        this.initStartingGameState(gameState);

        log.info("Created game. ");

        decryptoGameLogic.playGame();

        log.info("Done playing game. Log: ");
        log.info(gameState.toString());
    }

    public void reset() {
        this.decryptoGameLogic.reset();
    }

    public GameState getGameState() {
        return this.decryptoGameLogic.getGameState();
    }

    private void initStartingGameState(GameState gameState) {
        this.addPlayers(gameState);
        this.addKeywords(gameState);
    }

    private void addPlayers(GameState gameWorld) {
        List<String> playerNameList = this.fileReaderUtil.readEntriesForNewlineSeparatedFile(PLAYER_NAMES_FILE_NAME);
        this.randomizerUtil.shuffleCollection(playerNameList);

        String name = playerNameList.get(0);
        gameWorld.getTeam1().getPlayers().add(new Player(name));
        name = playerNameList.get(1);
        gameWorld.getTeam1().getPlayers().add(new Player(name));
        name = playerNameList.get(2);
        gameWorld.getTeam2().getPlayers().add(new Player(name));
        name = playerNameList.get(3);
        gameWorld.getTeam2().getPlayers().add(new Player(name));
        name = playerNameList.get(4);
    }

    private void addKeywords(GameState gameWorld) {
        List<String> keywords = this.fileReaderUtil.readEntriesForNewlineSeparatedFile(NOUNS_FILE_NAME);
        this.randomizerUtil.shuffleCollection(keywords);

        for (int i = 0; i < 4; i++) {
            gameWorld.getTeam1().getKeywords().add(keywords.get(i));
        }
        for (int i = 4; i < 8; i++) {
            gameWorld.getTeam2().getKeywords().add(keywords.get(i));
        }
    }
}
