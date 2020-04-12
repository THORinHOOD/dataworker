package com.thorinhood.dataworker.services.unite;

import com.thorinhood.dataworker.tables.profile.TwitterTable;
import com.thorinhood.dataworker.tables.profile.VKTable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocationSimilarity {

    public static double count(VKTable vk, TwitterTable twitter) {
        if (twitter.getLocation() == null) {
            return 0.0d;
        }
        String vkCity = vk.getCity();
        String vkCountry = vk.getCountry();

        List<String> twitterTokens = Stream.of(twitter.getLocation().split("\\s+"))
                .map(str -> str.replaceAll("[^\\p{L}\\p{Z}]",""))
                .collect(Collectors.toList());
        for (String token : twitterTokens) {
            if (token.equalsIgnoreCase(vkCity) || token.equalsIgnoreCase(vkCountry)) {
                return 1.0d;
            }
        }

        return 0.0d;
    }

}
