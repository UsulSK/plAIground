package org.usul.plaiground.app;

import com.google.inject.Inject;
import org.usul.plaiground.games.decrypto.Decrypto;

public class PlaigroundApp {

    @Inject
    private Decrypto decrypto;

    public void run() {
        decrypto.startGame();
    }
}
