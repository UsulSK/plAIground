package org.usul.plaiground.games.decrypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.usul.plaiground.backend.games.decrypto.DecryptoGame;
import org.usul.plaiground.backend.games.decrypto.DecryptoGameLogic;
import org.usul.plaiground.backend.games.decrypto.entities.GameLog;
import org.usul.plaiground.backend.games.decrypto.entities.GameState;
import org.usul.plaiground.backend.games.decrypto.llmroles.DecryptorLlm;
import org.usul.plaiground.backend.games.decrypto.llmroles.EncryptorLlm;
import org.usul.plaiground.backend.games.decrypto.llmroles.InterceptorLlm;
import org.usul.plaiground.utils.FileReaderUtil;
import org.usul.plaiground.utils.RandomizerUtil;
import org.usul.plaiground.utils.TestUtil;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DecryptoGameIntegrationTest {
    private EncryptorLlm encryptorLlm;

    private DecryptorLlm decryptorLlm;

    private InterceptorLlm interceptorLlm;

    private DecryptoGameLogic decryptoGameLogic;

    private FileReaderUtil fileReaderUtilMock;

    private RandomizerUtil randomizerUtilMock;

    private DecryptoGame sut;

    @BeforeEach
    void setupTests() {
        this.encryptorLlm = Mockito.mock(EncryptorLlm.class);
        this.decryptorLlm = Mockito.mock(DecryptorLlm.class);
        this.interceptorLlm = Mockito.mock(InterceptorLlm.class);

        this.decryptoGameLogic = new DecryptoGameLogic();
        TestUtil.setPrivateField(this.decryptoGameLogic, "brainEncryptor", this.encryptorLlm);
        TestUtil.setPrivateField(this.decryptoGameLogic, "brainDecryptor", this.decryptorLlm);
        TestUtil.setPrivateField(this.decryptoGameLogic, "brainInterceptor", this.interceptorLlm);

        this.fileReaderUtilMock = Mockito.mock(FileReaderUtil.class);
        this.randomizerUtilMock = Mockito.mock(RandomizerUtil.class);

        this.sut = new DecryptoGame();
        TestUtil.setPrivateField(this.sut, "fileReaderUtil", this.fileReaderUtilMock);
        TestUtil.setPrivateField(this.sut, "randomizerUtil", this.randomizerUtilMock);
        TestUtil.setPrivateField(this.sut, "decryptoGameLogic", this.decryptoGameLogic);

        List<String> mockedPlayerNames = new ArrayList<>(List.of("name1", "name2", "name3", "name4", "name5", "name6", "name7"));
        Mockito.lenient().when(this.fileReaderUtilMock.readEntriesForNewlineSeparatedFile(Mockito.eq(DecryptoGame.PLAYER_NAMES_FILE_NAME))).thenReturn(mockedPlayerNames);

        List<String> mockedNouns = new ArrayList<>(List.of("noun1", "noun2", "noun3", "noun4", "noun5", "noun6", "noun7", "noun8", "noun9", "noun10"));
        Mockito.lenient().when(this.fileReaderUtilMock.readEntriesForNewlineSeparatedFile(Mockito.eq(DecryptoGame.NOUNS_FILE_NAME))).thenReturn(mockedNouns);
    }

    private void mockGame() {

    }

    @Test
    void runGameWithAllReplicants() {

        // GIVEN

        this.mockGame();

        GameState gameState = this.sut.getGameState();
        GameLog gameLog = gameState.getGameLog();

        // WHEN

        this.sut.startGame(this::handleUpdateGameEvent);

        // THEN

        Assertions.assertFalse(gameLog.getRounds().isEmpty());
    }

    @Test
    void gameCanBeStartedTwice() {

        // GIVEN

        this.mockGame();
        GameState gameState = this.sut.getGameState();
        GameLog gameLog = gameState.getGameLog();

        // WHEN

        this.sut.startGame(this::handleUpdateGameEvent);
        this.sut.startGame(this::handleUpdateGameEvent);

        // THEN

        Assertions.assertFalse(gameLog.getRounds().isEmpty());
    }

    @Test
    void gameStateShouldBeEmptyAfterPlayingWhenResetting() {

        // GIVEN

        GameState gameState = this.sut.getGameState();
        GameLog gameLog = gameState.getGameLog();
        this.mockGame();

        // WHEN

        this.sut.startGame(this::handleUpdateGameEvent);
        Assertions.assertFalse(gameLog.getRounds().isEmpty());
        this.sut.reset();

        // THEN the state of the game should be empty

        Assertions.assertTrue(gameLog.getRounds().isEmpty());
        Assertions.assertNull(gameState.getWinningTeam());
    }

    @Test
    void gameStateShouldBeEmptyAtBeginning() {

        // WHEN game has not started yet

        GameState gameState = this.sut.getGameState();

        // THEN the state of the game should be empty

        GameLog gameLog = gameState.getGameLog();
        Assertions.assertTrue(gameLog.getRounds().isEmpty());
        Assertions.assertNull(gameState.getWinningTeam());
    }

    private void handleUpdateGameEvent() {

    }
}
