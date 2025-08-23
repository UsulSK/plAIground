package org.usul.plaiground.backend.app;

import com.google.inject.Inject;
import org.usul.plaiground.backend.games.decrypto.DecryptoGame;

public class PlaigroundApp {

    @Inject
    private DecryptoGame decryptoGame;

    public DecryptoGame runDecrypto(Runnable gameUpdateListener) {
        this.decryptoGame.startGame(gameUpdateListener);

        return  this.decryptoGame;
    }

    public DecryptoGame getDecryptoGame() {
        return this.decryptoGame;
    }

}
