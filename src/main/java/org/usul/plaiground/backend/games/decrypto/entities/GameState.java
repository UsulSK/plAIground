package org.usul.plaiground.backend.games.decrypto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameState extends ParentEntity {

    private Team team1;
    private Team team2;

    private final GameLog gameLog = new GameLog();

    @JsonIgnore
    private Team winningTeam = null;

    public void reset() {
        this.team1 = new Team("Tyrell");
        this.team2 = new Team("Delos");
        this.gameLog.reset();
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
}
