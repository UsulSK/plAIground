package org.usul.plaiground.utils;

import org.json.JSONObject;
import org.usul.plaiground.backend.games.decrypto.entities.*;
import org.usul.plaiground.backend.outbound.llm.request.RequestBodyBuilderUtil;


public class LlmRequestBodyBuilderForTesting {

    /*
     * This can be used to generate the requests for the LLM for you to copy to a HTTP client in order to test against KobolCpp LLm.
     */
    public static void main(String[] args) throws Exception {
        FileReaderUtil fileReaderUtil = new FileReaderUtil();

        String promptForTest = fileReaderUtil.readTextFile("prompt_test");

        JSONObject bodyJson = RequestBodyBuilderUtil.buildJsonBody(promptForTest, 400, 0.7F, .9F,123);

        System.out.println("\n\nPrompt for request:\n\n" + bodyJson.toString(4) + "\n\n");
    }


}
