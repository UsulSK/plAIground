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
@JsonPropertyOrder({ "encryptor", "decryptor", "code", "encryptedCode" })
public class TeamRound {
    @JsonIgnore
    private Player encryptor;

    @JsonIgnore
    private Player decryptor;

    private List<Integer> code = new ArrayList<>();
    private List<String> encryptedCode = new ArrayList<>();
    private List<Integer> guessedCodeByOtherTeam = new ArrayList<>();
    private List<Integer> guessedCodeByOwnTeam = new ArrayList<>();

    @JsonProperty("encryptor")
    public String getEncryptorStartingTeamForSerialization() {
        return this.encryptor.getName();
    }

    @JsonProperty("decryptor")
    public String getDecryptorStartingTeamForSerialization() {
        return this.decryptor.getName();
    }
}
