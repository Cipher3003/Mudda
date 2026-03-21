package com.mudda.backend.social;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to parse @username mentions from user-generated text.
 */
public class MentionParser {

    // Matches @ followed by word characters (alphanumeric + underscore).
    // Stops at spaces, punctuation, or end of string.
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    private MentionParser() {}

    /**
     * Extracts unique usernames mentioned in the text (without the @ symbol).
     */
    public static Set<String> extractUsernames(String text) {
        Set<String> usernames = new HashSet<>();
        if (text == null || text.isBlank()) {
            return usernames;
        }

        Matcher matcher = MENTION_PATTERN.matcher(text);
        while (matcher.find()) {
            usernames.add(matcher.group(1));
        }

        return usernames;
    }
}
