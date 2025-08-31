package org.usul.plaiground.frontend.gameviews.decrypto;

import org.usul.plaiground.backend.games.decrypto.entities.GameState;
import org.usul.plaiground.backend.games.decrypto.entities.Player;
import org.usul.plaiground.backend.games.decrypto.entities.Team;

import javax.swing.*;
import java.awt.*;
import java.util.List;
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

        JScrollPane scrollPane = new JScrollPane(
                this.teamText,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.getViewport().setBackground(Color.BLACK);

        this.add(scrollPane, BorderLayout.CENTER);

        this.updateView();

        this.setBackground(Color.BLACK);
    }

    public void updateView() {
        String teamInfoText = "<html><b>" + this.team.getName() + "</b>";
        teamInfoText += "<br><br>";
        teamInfoText += "<table>";

        int keywordNr = 0;
        for (String keyword : this.team.getKeywords()) {
            teamInfoText += "<tr>";
            teamInfoText += "<td><b>";
            teamInfoText += keyword;
            teamInfoText += "</b></td>";
            teamInfoText += "<td>";
            teamInfoText += this.createPastCluesText(this.gameState.getGameLog().getPastCluesForCodeDigit(keywordNr + 1, this.team));
            teamInfoText += "</td>";
            teamInfoText += "</tr>";
            keywordNr++;
        }
        teamInfoText += "</table>";

        teamInfoText += "<br>";

        teamInfoText += this.gameState.getInterceptTokensForTeam(this.team) + " intercept tokens";
        teamInfoText += "<br>";

        teamInfoText += this.gameState.getMiscommunicationTokensForTeam(this.team) + " miscommunication tokens";
        teamInfoText += "<br>";

        teamInfoText += "=> <b>" + this.gameState.getTotalPointsForTeam(this.team) + "</b>";
        teamInfoText += "<br>";
        teamInfoText += "</html>";

        this.teamText.setText(teamInfoText);
    }

    private String createPastCluesText(List<String> pastClues) {
        if (pastClues.isEmpty()) {
            return "-";
        }

        return pastClues.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "));
    }
}
