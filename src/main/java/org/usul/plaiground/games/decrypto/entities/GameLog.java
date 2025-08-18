package org.usul.plaiground.games.decrypto.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameLog extends ParentEntity {
    private List<Round> rounds = new ArrayList<>();

    public void addRound() {
        this.rounds.add(new Round());
    }
}
