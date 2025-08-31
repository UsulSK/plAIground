package org.usul.plaiground.backend.games.decrypto.llmroles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EncryptResponse implements ILlmResponse {
    List<String> clues = new ArrayList<>();
    List<String> reasonsTeammate = new ArrayList<>();
    List<String> reasonsOpponent = new ArrayList<>();
}
