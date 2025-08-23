package org.usul.plaiground.frontend.gameviews.decrypto;

import org.usul.plaiground.backend.games.decrypto.entities.GameState;

import javax.swing.*;
import java.awt.*;

public class OverviewView extends JPanel {

    private GameState gameState;

    private final TeamView teamView1;
    private final TeamView teamView2;

    public OverviewView(GameState gameState) {
        super(new GridLayout(1, 2));

        this.gameState = gameState;

        this.teamView1 = new TeamView(gameState, gameState.getTeam1());
        this.teamView2 = new TeamView(gameState, gameState.getTeam2());

        this.add(teamView1);
        this.add(teamView2);

        this.setBackground(Color.BLACK);
    }

    public void updateView() {
        this.teamView1.updateView();
        this.teamView2.updateView();
    }
}
