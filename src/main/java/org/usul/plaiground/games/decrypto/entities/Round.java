package org.usul.plaiground.games.decrypto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonPropertyOrder({ "roundNumber", "startingTeam", "teamInfo" })
public class Round extends ParentEntity {
    private Integer roundNumber = 0;

    @JsonIgnore
    private Team startingTeam;

    private Map<String, TeamRound> teamInfo = new HashMap<>();

    @JsonProperty("startingTeam")
    public String getStartingTeamForSerialization() {
        return startingTeam.getName();
    }
}
