package org.usul.plaiground.games.decrypto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({ "players", "keywords" })
public class Team extends ParentEntity {
    private String name;

    @JsonIgnore
    private List<Player> players = new ArrayList<>();

    private List<String> keywords = new ArrayList<>();

    private int miscommunicationTokens = 0;
    private int interceptionTokens = 0;

    public Team(String name) {
        this.name = name;
    }

    public Player getOtherPlayer(Player player) {
        if (this.players.getFirst() == player) {
            return this.players.getLast();
        }
        return this.players.getFirst();
    }

    @JsonProperty("players")
    public List<String> getPlayersForSerialization() {
        return players.stream()
                .map(Player::getName)
                .toList();
    }

    public void addMiscommunicationToken() {
        this.miscommunicationTokens++;
    }

    public void addInterceptionTokensToken() {
        this.interceptionTokens++;
    }
}
