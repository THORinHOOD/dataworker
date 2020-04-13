package com.thorinhood.dataworker.services.unite;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.CosineSimilarity;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CosineService {

    private final Pattern PATTERN = Pattern.compile("\\S+");
    private final CosineSimilarity cosineSimilarity = new CosineSimilarity();

    public double similarity(List<String> first, List<String> second) {
        final List<String> firstTokens = tokenize(first);
        final List<String> secondTokens = tokenize(second);
        final Map<CharSequence, Integer> leftVector = of(firstTokens);
        final Map<CharSequence, Integer> rightVector = of(secondTokens);
        final double similarity = cosineSimilarity.cosineSimilarity(leftVector, rightVector);
        return similarity;
    }

    private List<String> tokenize(List<String> texts) {
        return texts.stream()
            .flatMap(text -> tokenize(text).stream())
            .filter(token -> !token.isEmpty())
            .collect(Collectors.toList());
    }

    private List<String> tokenize(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        final Matcher matcher = PATTERN.matcher(text);
        final List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            String[] subTokens = matcher.group(0).replaceAll("[^\\p{L}\\p{Z}]", " ").split(" ");
            for (String token : subTokens) {
                if (token != null && !token.isEmpty()) {
                    tokens.add(token);
                }
            }
        }
        return tokens;
    }

    private Map<CharSequence, Integer> of(List<String> tokens) {
        final Map<CharSequence, Integer> innerCounter = new HashMap<>();
        for (String token : tokens) {
            if (innerCounter.containsKey(token)) {
                int value = innerCounter.get(token);
                innerCounter.put(token, ++value);
            } else {
                innerCounter.put(token, 1);
            }
        }
        return innerCounter;
    }

}
