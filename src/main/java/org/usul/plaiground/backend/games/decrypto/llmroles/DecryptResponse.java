package org.usul.plaiground.backend.games.decrypto.llmroles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DecryptResponse implements ILlmResponse {
    List<Integer> code = new ArrayList<>();
    private List<String> decryptReasons = new ArrayList<>();
}
