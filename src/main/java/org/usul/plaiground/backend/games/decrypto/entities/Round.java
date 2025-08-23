package org.usul.plaiground.backend.games.decrypto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@JsonPropertyOrder({"roundNumber", "startingTeam", "tokens", "teamInfo"})
public class Round extends ParentEntity {
    private Integer roundNumber = 0;

    @JsonIgnore
    private Team startingTeam;

    @JsonIgnore
    private Team team1;
    @JsonIgnore
    private Team team2;

    @JsonIgnore
    private int team1MissCommTokens = 0;
    @JsonIgnore
    private int team1InterceptTokens = 0;
    @JsonIgnore
    private int team2MissCommTokens = 0;
    @JsonIgnore
    private int team2InterceptTokens = 0;

    private Map<String, TeamRound> teamInfo = new HashMap<>();

    @JsonProperty("startingTeam")
    public String getStartingTeamForSerialization() {
        return startingTeam.getName();
    }

    @JsonProperty("tokens")
    public String getTokens() {
        return this.team1.getName() + ": miscom: " + this.team1MissCommTokens + ", intercept: " + this.team1InterceptTokens
                + " - " + this.team2.getName() + ": miscom: " + this.team2MissCommTokens + ", intercept: " + this.team2InterceptTokens;
    }

    public void addMiscommunicationToken(Team team) {
        if (team == team1) {
            this.team1MissCommTokens++;
            return;
        }
        this.team2MissCommTokens++;
    }

    public void addInterceptionTokensToken(Team team) {
        if (team == team1) {
            this.team1InterceptTokens++;
            return;
        }
        this.team2InterceptTokens++;
    }
}
