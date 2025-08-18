package org.usul.plaiground.outbound.llm.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KoboldLlmResponse {
    private List<Result> results;
}
