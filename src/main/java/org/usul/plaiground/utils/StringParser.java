package org.usul.plaiground.utils;

public class StringParser {

    public static String parseJson(String input) {
        if (!input.contains("{") || !input.contains("}")) {
            return null;
        }

        int start = input.indexOf('{');
        int end = input.lastIndexOf('}');
        String jsonPart = input.substring(start, end + 1);

        return jsonPart;
    }
}
