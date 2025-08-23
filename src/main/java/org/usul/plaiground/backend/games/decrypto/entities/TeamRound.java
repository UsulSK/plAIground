package org.usul.plaiground.backend.games.decrypto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({ "encryptor", "decryptor", "code", "encryptedCode" })
public class TeamRound {
    private List<Integer> code = new ArrayList<>();
    private List<String> encryptedCode = new ArrayList<>();
    private List<Integer> guessedCodeByOtherTeam = new ArrayList<>();
    private List<Integer> guessedCodeByOwnTeam = new ArrayList<>();
}
