package org.usul.plaiground.frontend.gameviews.decrypto;

import org.usul.plaiground.backend.games.decrypto.entities.GameState;
import org.usul.plaiground.backend.games.decrypto.entities.Player;
import org.usul.plaiground.backend.games.decrypto.entities.Team;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class TeamView extends JPanel {
    private GameState gameState;
    private Team team;
    private JLabel teamText = new JLabel();

    public TeamView(GameState gameState, Team team) {
        super(new BorderLayout());

        this.gameState = gameState;
        this.team = team;

        teamText.setFont(new Font("Arial", Font.PLAIN, 15));
        teamText.setHorizontalAlignment(SwingConstants.LEFT);
        teamText.setVerticalAlignment(SwingConstants.TOP);

        teamText.setForeground(Color.CYAN);

        this.add(this.teamText, BorderLayout.CENTER);

        this.updateView();

        this.setBackground(Color.BLACK);
    }

    public void updateView() {
        String teamInfoText = "<html><b>" + this.team.getName() + "</b>";
        teamInfoText += "<br><br>";
        for (Player player : this.team.getPlayers()) {
            teamInfoText += "[" + player.getName() + "] ";
        }
        teamInfoText += "<br><br>";

        String keywords = this.team.getKeywords().stream().map(s -> "<b>" + s + "</b>").collect(Collectors.joining(" "));
        ;
        teamInfoText += keywords;
        teamInfoText += "<br><br>";

        teamInfoText += this.gameState.getInterceptTokensForTeam(this.team) + " intercept tokens";
        teamInfoText += "<br>";

        teamInfoText += this.gameState.getMiscommunicationTokensForTeam(this.team) + " miscommunication tokens";
        teamInfoText += "<br>";

        teamInfoText += "=> <b>" + this.gameState.getTotalPointsForTeam(this.team) + "</b>";
        teamInfoText += "<br>";
        teamInfoText += "</html>";

        this.teamText.setText(teamInfoText);
    }
}
