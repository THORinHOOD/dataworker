package com.thorinhood.dataworker.services.unite;

import java.util.Map;

public class FriendsSimilarity {

    public static double count(Map<String, String> first, Map<String, String> second) {
        int tmp = Math.min(first.size(), second.size());
        if (tmp == 0) {
            return 0;
        }
        return (double) intersection(first, second) / (double) tmp;
    }

    private static int intersection(Map<String, String> first, Map<String, String> second) {
        Map<String, String> less, greater;
        if (first.size() <= second.size()) {
            less = first;
            greater = second;
        } else {
            less = second;
            greater = first;
        }
        return less.values().stream()
                .map(value -> greater.containsKey(value) ? 1 : 0)
                .reduce(0, Integer::sum);
    }

}
