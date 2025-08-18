package org.usul.plaiground.games.decrypto.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player extends ParentEntity {
    private String name;

    public Player(String name) {
        this.name = name;
    }
}
