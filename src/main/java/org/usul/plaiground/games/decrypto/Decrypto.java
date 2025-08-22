package org.usul.plaiground.games.decrypto;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usul.plaiground.games.decrypto.entities.GameWorld;
import org.usul.plaiground.games.decrypto.entities.Player;
import org.usul.plaiground.outbound.llm.KoboldLlmConnector;
import org.usul.plaiground.utils.FileReader;

import java.util.*;

public class Decrypto {

    private static final Logger log = LoggerFactory.getLogger(Decrypto.class);

    @Inject
    FileReader fileReader;

    @Inject
    GameMaster gameMaster;

    @Inject
    KoboldLlmConnector llm;

    public void startGame() {
        log.info("starting game Decrypto");

        GameWorld gameWorld = this.createGameWorld();

        log.info("Created game. ");

        this.gameMaster.setGameWorld(gameWorld);
        gameMaster.playGame();

        log.info("Done playing game. Log: ");
        log.info(gameWorld.toString());
    }

    private GameWorld createGameWorld() {
        GameWorld gameWorld = new GameWorld();
        this.addPlayers(gameWorld);
        this.addKeywords(gameWorld);

        return gameWorld;
    }

    private void addPlayers(GameWorld gameWorld) {
        List<String> playerNameList = this.fileReader.readEntriesForNewlineSeparatedFile("decrypto/playernames");
        Collections.shuffle(playerNameList, new Random(System.currentTimeMillis()));

        String name = playerNameList.get(0);
        gameWorld.getTeam1().getPlayers().add(new Player(name));
        name = playerNameList.get(1);
        gameWorld.getTeam1().getPlayers().add(new Player(name));
        name = playerNameList.get(2);
        gameWorld.getTeam2().getPlayers().add(new Player(name));
        name = playerNameList.get(3);
        gameWorld.getTeam2().getPlayers().add(new Player(name));
        name = playerNameList.get(4);
        gameWorld.setJudge(new Player(name));
    }

    private void addKeywords(GameWorld gameWorld) {
        List<String> keywords = this.fileReader.readEntriesForNewlineSeparatedFile("decrypto/nouns");
        Collections.shuffle(keywords);

        for (int i = 0; i < 4; i++) {
            gameWorld.getTeam1().getKeywords().add(keywords.get(i));
        }
        for (int i = 4; i < 8; i++) {
            gameWorld.getTeam2().getKeywords().add(keywords.get(i));
        }
    }
}
