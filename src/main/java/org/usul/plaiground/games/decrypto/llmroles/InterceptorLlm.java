package org.usul.plaiground.games.decrypto.llmroles;

import com.google.inject.Inject;
import org.usul.plaiground.games.decrypto.entities.GameWorld;
import org.usul.plaiground.games.decrypto.entities.Team;
import org.usul.plaiground.outbound.llm.KoboldLlmConnector;
import org.usul.plaiground.utils.FileReader;

import java.util.ArrayList;
import java.util.List;

public class InterceptorLlm {

    private GameWorld gameWorld;

    @Inject
    private FileReader fileReader;

    @Inject
    KoboldLlmConnector llm;

    public List<Integer> intercept(Team interceptingTeam, List<String> encryptedCode, int roundNumber) {
        return new ArrayList<>(List.of(4, 3, 2));
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }
}
