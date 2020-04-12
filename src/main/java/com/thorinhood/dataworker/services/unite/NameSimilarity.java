package com.thorinhood.dataworker.services.unite;

import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.tables.profile.VKTable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NameSimilarity {

    public static double count(VKTable vk, TwitterTable twitter) {
        if (twitter.getName() == null) {
            return 0.0d;
        }
        if (vk.getFirstName() == null || vk.getLastName() == null) {
            return 0.0d;
        }

        List<String> twitterTokens = Stream.of(twitter.getName().split("\\s+"))
                .map(str -> str.replaceAll("[^\\p{L}\\p{Z}]",""))
                .collect(Collectors.toList());
        for (String token : twitterTokens) {
            if (token.equalsIgnoreCase(vk.getFirstName()) || token.equalsIgnoreCase(vk.getLastName())) {
                return 1.0d;
            }
        }

        return 0.0d;
    }

}
