package org.usul;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args)  throws Exception {
        String baseUrl = "http://127.0.0.1:5001"; // KoboldCpp API
        String endpoint = "/api/v1/generate";

        // Create JSON payload similar to your PowerShell example
        JSONObject payload = new JSONObject();
        payload.put("prompt", "Say three words.");
        payload.put("max_length", 50);
        payload.put("temperature", 0.7);
        payload.put("seed", -1);  // random seed for different results each call

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        // Send request
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Print response
        System.out.println("Response from KoboldCpp LLM:\n" + response.body());
    }
}