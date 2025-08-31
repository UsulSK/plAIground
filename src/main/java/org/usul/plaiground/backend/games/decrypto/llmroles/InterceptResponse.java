package org.usul.plaiground.backend.games.decrypto.llmroles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InterceptResponse implements ILlmResponse {
    List<Integer> guessedCode = new ArrayList<>();
    List<String> reasons = new ArrayList<>();
    List<String> guessedSecretWords = new ArrayList<>();
}
