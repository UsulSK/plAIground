package org.usul.plaiground.backend.games.decrypto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameState extends ParentEntity {
    private Team team1 = new Team();
    private Team team2 = new Team();

    private final GameLog gameLog = new GameLog();

    private boolean isGameFinished = false;

    @JsonIgnore
    private Team winningTeam = null;

    public void reset() {
        this.team1.setName("Tyrell");
        this.team2.setName("Delos");
        boolean isGameFinished = false;

        this.gameLog.reset();
    }

    public int getMiscommunicationTokensForTeam(Team team) {
        if (this.gameLog.getRounds().isEmpty()) {
            return 0;
        }

        int tokens = 0;
        if (team == this.getTeam1()) {
            tokens = this.gameLog.getRounds().getLast().getTeam1MissCommTokens();
        } else {
            tokens = this.gameLog.getRounds().getLast().getTeam2MissCommTokens();
        }

        return tokens;
    }

    public int getInterceptTokensForTeam(Team team) {
        if (this.gameLog.getRounds().isEmpty()) {
            return 0;
        }

        int tokens = 0;
        if (team == this.getTeam1()) {
            tokens = this.gameLog.getRounds().getLast().getTeam1InterceptTokens();
        } else {
            tokens = this.gameLog.getRounds().getLast().getTeam2InterceptTokens();
        }

        return tokens;
    }

    public int getTotalPointsForTeam(Team team) {
        return this.getInterceptTokensForTeam(team) - this.getMiscommunicationTokensForTeam(team);
    }

    public Team getOtherTeam(Team team) {
        if (team == team1) {
            return team2;
        }
        return team1;
    }

    @JsonProperty("winningTeam")
    public String getWinningTeamForSerialization() {
        if (this.winningTeam == null) {
            return "none: TIE";
        } else {
            return this.winningTeam.getName();
        }
    }

    public Team getTeamOfPlayer(Player player) {
        if (this.team1.getPlayers().contains(player)) {
            return this.team1;
        }
        return this.team2;
    }

    public int getRoundNumber() {
        return this.gameLog.getRounds().size() - 1;
    }
}
