package org.usul.plaiground.backend.outbound.llm.request;

import org.json.JSONObject;

public class RequestBodyBuilderUtil {

    public static JSONObject buildJsonBody(String msg, int maxLength, float temperature, int seed) {
        JSONObject payload = new JSONObject();
        payload.put("prompt", msg);
        payload.put("max_length", maxLength);
        payload.put("temperature", temperature);
        payload.put("seed", seed);

        return payload;
    }
}
