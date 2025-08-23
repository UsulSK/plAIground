package org.usul.plaiground.frontend.gameviews.decrypto;

import org.usul.plaiground.backend.app.PlaigroundApp;
import org.usul.plaiground.backend.games.decrypto.entities.GameState;

import javax.swing.*;
import java.awt.*;

public class DecryptoMainView extends JPanel {

    private final PlaigroundApp app;

    private final OverviewView overviewView;
    private final RoundView roundView;

    public DecryptoMainView(PlaigroundApp app) {
        super(new BorderLayout());

        this.app = app;

        this.setBackground(Color.BLACK);

        GameState gameState = app.getDecryptoGame().getGameState();

        this.roundView = new RoundView(gameState);
        this.overviewView = new OverviewView(gameState);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, roundView, overviewView);
        split.setResizeWeight(1);

        this.add(split, BorderLayout.CENTER);

        this.startGame();
    }

    private void startGame() {
        Thread t = new Thread(() -> {
            this.app.runDecrypto(this::handleGameUpdateEvent);
        });
        t.start();
    }

    private void handleGameUpdateEvent() {
        SwingUtilities.invokeLater(this::handleGameUpdateEventThreadSafe);
    }

    private void handleGameUpdateEventThreadSafe() {
        this.roundView.updateView();
        this.overviewView.updateView();

        this.revalidate();
        this.repaint();
    }
}
