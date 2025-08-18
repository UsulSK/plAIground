package org.usul.plaiground.outbound.llm.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Result {
    private String text;
    private List<Object> tool_calls;
    private String finish_reason;
    private Integer prompt_tokens;
    private Integer completion_tokens;
    private Object logprobs;
}
