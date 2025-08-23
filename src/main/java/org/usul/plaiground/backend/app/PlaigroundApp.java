package org.usul.plaiground.backend.app;

import com.google.inject.Inject;
import org.usul.plaiground.backend.games.decrypto.DecryptoGame;

public class PlaigroundApp {

    @Inject
    private DecryptoGame decryptoGame;

    public void run() {
        decryptoGame.startGame();
    }
}
