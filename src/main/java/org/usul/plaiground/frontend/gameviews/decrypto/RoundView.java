package org.usul.plaiground.frontend.gameviews.decrypto;

import org.usul.plaiground.backend.games.decrypto.entities.*;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class RoundView extends JPanel {

    private final GameState gameState;

    private final JEditorPane roundText = new JEditorPane();

    public RoundView(GameState gameState) {
        super(new BorderLayout());

        this.gameState = gameState;

        this.roundText.setEditable(false);
        this.roundText.setContentType("text/html");
        this.roundText.setForeground(Color.MAGENTA);
        this.roundText.setBackground(Color.BLACK);

        this.roundText.setFont(new Font("Arial", Font.PLAIN, 40));

        this.updateView();

        this.setBackground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(this.roundText);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void updateView() {
        StringBuilder roundTextSB = new StringBuilder();
        roundTextSB.append("<html><body style='font-family:Arial; font-size:15pt;'>");
        roundTextSB.append("<font color='#FF00FF'>");

        GameLog gameLog = this.gameState.getGameLog();

        for (Round round : gameLog.getRounds()) {
            {
                roundTextSB.append("<b>ROUND ");
                roundTextSB.append(round.getRoundNumber() + 1);
                roundTextSB.append("</b>");
                roundTextSB.append("<br><br>");

                TeamRound teamRound1 = round.getTeamInfo().get(this.gameState.getTeam1().getName());
                TeamRound teamRound2 = round.getTeamInfo().get(this.gameState.getTeam2().getName());
                this.addTextForTeamRound(this.gameState.getTeam1(), teamRound1, roundTextSB);
                roundTextSB.append("<br>");
                this.addTextForTeamRound(this.gameState.getTeam2(), teamRound2, roundTextSB);

                roundTextSB.append("<br><br>");
            }
        }

        this.addGameWinnerText(roundTextSB);

        roundTextSB.append("</font></body></html>");

        this.roundText.setText(roundTextSB.toString());
    }

    private void addGameWinnerText(StringBuilder sb) {
        if (!this.gameState.isGameFinished()) {
            return;
        }

        if (this.gameState.getWinningTeam() == null) {
            sb.append("<br><br>");
            sb.append("<b>DRAW!</b>");
        } else {
            sb.append("<br><br>");
            sb.append("<b>WINNER: ");
            sb.append(this.gameState.getWinningTeam().getName());
            sb.append("</b>");
        }
    }


    private void addTextForTeamRound(Team team, TeamRound teamRound, StringBuilder sb) {
        if (teamRound == null) {
            return;
        }

        sb.append("<b>");
        sb.append(team.getName());
        sb.append("</b>");
        sb.append(":");
        sb.append("<br>");

        sb.append("Code: ");
        String codeText = teamRound.getCode().stream().map(s -> "<b>" + s + "</b>").collect(Collectors.joining(" "));
        sb.append(codeText);
        sb.append("<br>");

        if (!teamRound.getEncryptedCode().isEmpty()) {
            sb.append("Clues: ");
            String clues = teamRound.getEncryptedCode().stream().map(s -> "<b>[" + s + "]</b>").collect(Collectors.joining(" "));
            sb.append(clues);
            sb.append("<br>");
            int clueNr = 0;
            for (String clue : teamRound.getEncryptedCode()) {
                int code = teamRound.getCode().get(clueNr);
                sb.append("&nbsp;&nbsp;&nbsp;[");
                sb.append(code);
                sb.append(" - ");
                sb.append(clue);
                sb.append(" - ");
                sb.append(team.getKeywords().get(code - 1));
                sb.append("] Reason that helps teammate: \"");
                sb.append(teamRound.getEncryptedCodeReasons().get(clueNr));
                sb.append("\"");
                sb.append("<br>");
                if (!teamRound.getEncryptedCodeOpponentReasons().isEmpty()) {
                    sb.append("&nbsp;&nbsp;&nbsp;[");
                    sb.append(code);
                    sb.append(" - ");
                    sb.append(clue);
                    sb.append(" - ");
                    sb.append(team.getKeywords().get(code - 1));
                    sb.append("] Reason that confuses opponent: \"");
                    sb.append(teamRound.getEncryptedCodeOpponentReasons().get(clueNr));
                    sb.append("\"");
                    sb.append("<br>");
                }
                clueNr++;
            }
        }

        if (!teamRound.getGuessedCodeByOtherTeam().isEmpty()) {
            sb.append("Intercept Guess: ");
            String intercept = teamRound.getGuessedCodeByOtherTeam().stream().map(s -> "<b>" + s + "</b>").collect(Collectors.joining(" "));
            sb.append(intercept);
            if (teamRound.getGuessedCodeByOtherTeam().equals(teamRound.getCode())) {
                sb.append(" <b>=> SUCCESS!</b>");
            }
            sb.append("<br>");

            int clueNr = 0;
            for (String clue : teamRound.getEncryptedCode()) {
                sb.append("&nbsp;&nbsp;&nbsp;[");
                sb.append(clue);
                sb.append(" - ");
                sb.append(teamRound.getGuessedCodeByOtherTeam().get(clueNr));
                sb.append("] Reason for guess: \"");
                sb.append(teamRound.getInterceptReasons().get(clueNr));
                sb.append("\"");

                sb.append("<br>");
                sb.append("&nbsp;&nbsp;&nbsp;[");
                sb.append(clue);
                sb.append(" - ");
                sb.append(teamRound.getGuessedCodeByOtherTeam().get(clueNr));
                sb.append("] Guessed secret word: \"");
                sb.append(teamRound.getInterceptKeywordGuess().get(clueNr));
                sb.append("\"");
                sb.append("<br>");
                clueNr++;
            }
        }

        if (!teamRound.getGuessedCodeByOwnTeam().isEmpty()) {
            sb.append("Guessed: ");
            String guessed = teamRound.getGuessedCodeByOwnTeam().stream().map(s -> "<b>" + s + "</b>").collect(Collectors.joining(" "));
            sb.append(guessed);
            if (!teamRound.getGuessedCodeByOwnTeam().equals(teamRound.getCode())) {
                sb.append(" <b>=> MISSCOMMUNICATION!</b>");
            }
            sb.append("<br>");

            int clueNr = 0;
            for (String clue : teamRound.getEncryptedCode()) {
                sb.append("&nbsp;&nbsp;&nbsp;[");
                sb.append(clue);
                sb.append(" - ");
                sb.append(teamRound.getGuessedCodeByOwnTeam().get(clueNr));
                sb.append("] Reason for guess: \"");
                sb.append(teamRound.getDecryptReasons().get(clueNr));
                sb.append("\"");
                sb.append("<br>");
                clueNr++;
            }
        }
    }

}
