package org.usul.plaiground.backend.outbound.llm.request;

import org.json.JSONObject;

import java.util.List;

public class RequestBodyBuilderUtil {

    public static JSONObject buildJsonBody(String msg, int maxLength, float temperature, float top_p, int seed) {
        JSONObject payload = new JSONObject();
        payload.put("prompt", msg);
        payload.put("max_length", maxLength);
        payload.put("temperature", temperature);
        payload.put("seed", seed);
        payload.put("top_p", top_p);
        payload.put("stop_sequence", List.of("[END]"));

        return payload;
    }
}
