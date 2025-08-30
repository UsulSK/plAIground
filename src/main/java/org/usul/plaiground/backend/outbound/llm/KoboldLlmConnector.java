package org.usul.plaiground.backend.outbound.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usul.plaiground.backend.outbound.llm.request.RequestBodyBuilderUtil;
import org.usul.plaiground.backend.outbound.llm.response.KoboldLlmResponse;

public class KoboldLlmConnector {
    private static final Logger log = LoggerFactory.getLogger(KoboldLlmConnector.class);

    public String chat(String msg) {
        return this.chat(msg, 350, 0.7F, 0.9F);
    }

    public String chat(String msg, int maxLength, float temperature, float top_p) {
        String baseUrl = "http://127.0.0.1:5001"; // KoboldCpp API
        String endpoint = "/api/v1/generate";

        Random random = new Random(System.currentTimeMillis());
        int seed = random.nextInt();

        JSONObject payload = RequestBodyBuilderUtil.buildJsonBody(msg, maxLength, temperature, top_p, seed);

        log.info("llm_payload: \n" + payload.toString(4));

        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("llm_contact_error!", e);
            throw new RuntimeException(e);
        }
        String responseText = response.body();
        log.info("original_llm_answer: " + responseText);

        ObjectMapper mapper = new ObjectMapper();
        KoboldLlmResponse parsedReponse = null;
        try {
            parsedReponse = mapper.readValue(responseText, KoboldLlmResponse.class);
        } catch (Exception e) {
            log.error("llm_json_parse_error!", e);
            throw new RuntimeException(e);
        }

        String textResponse = parsedReponse.getResults().get(0).getText();

        log.info("llm_answer: \n" + textResponse);

        return textResponse;
    }
}
