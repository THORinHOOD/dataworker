package com.thorinhood.dataworker.utils.common;

import com.thorinhood.dataworker.tables.profile.VKTable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinksUtil {

    private final static String TWITTER = "://twitter.com/";
    private final static String FACEBOOK = "://www.facebook.com/";
    private final static String INSTAGRAM = "://instagram.com/";

    private static final Map<String, String> patterns = Map.of(
            "vk", "http(s?)://vk.com/(.*)",
            "twitter", "http(s?)://twitter.com/(.*)",
            "facebook", "http(s?)://facebook.com/(.*)",
            "instagram", "http(s?)://instagram.com/(.*)"
    );

    private final static Collection<BiConsumer<Map<String, String>, VKTable>> assumptionsSetters = Arrays.asList(
            LinksUtil::getFacebook,
            LinksUtil::getInstagram,
            LinksUtil::getTwitter
    );

    public static void extractLinks(VKTable vkTable) {
        String about;
        if (vkTable.getAbout() == null || vkTable.getAbout().isEmpty()) {
            return;
        }
        about = vkTable.getAbout();
        String[] lines = about.split("\n");
        for (String line : lines) {
            if (line.contains(TWITTER)) {
                extractLink(vkTable, patterns.get("twitter"), 2, VKTable::getTwitter,
                    VKTable::setTwitter, line);
            } else if (line.contains(FACEBOOK)) {
                extractLink(vkTable, patterns.get("facebook"), 2, VKTable::getFacebook,
                    VKTable::setFacebook, line);
            } else if (line.contains(INSTAGRAM)) {
                extractLink(vkTable, patterns.get("instagram"), 2, VKTable::getInstagram,
                    VKTable::setInstagram, line);
            }
        }
    }

    public static String extractId(String socialNetwork, String link) {
        if (socialNetwork == null || socialNetwork.isBlank()) {
            return null;
        }
        String pattern = patterns.getOrDefault(socialNetwork.toLowerCase(), null);
        return pattern == null ? null : extractIdLink(pattern, 2, link);
    }

    public static void getAllAssumptions(Map<String, String> assumptions, VKTable vkTable) {
        assumptionsSetters.forEach(setter -> setter.accept(assumptions, vkTable));
    }

    public static void getFacebook(Map<String, String> assumptions, VKTable vkTable) {
        generateLink(assumptions, "facebook", vkTable, "https://www.facebook.com/",
                VKTable::getFacebook);
    }

    public static void getInstagram(Map<String, String> assumptions, VKTable vkTable) {
        generateLink(assumptions, "instagram", vkTable, "https://www.instagram.com/",
                VKTable::getInstagram);
    }

    public static void getTwitter(Map<String, String> assumptions, VKTable vkTable) {
        generateLink(assumptions, "twitter", vkTable, "http://twitter.com/", VKTable::getTwitter);
    }

    public static void generateLink(Map<String, String> assumptions, String key, VKTable vkTable, String pattern,
                                    Function<VKTable, String> getter) {
        String id = getter.apply(vkTable);
        if (!(id == null || id.isEmpty())) {
            assumptions.put(key, pattern + id);
        }
    }

    private static void extractLink(VKTable vkTable, String pattern, int group, Function<VKTable, String> getOldValue,
                                    BiConsumer<VKTable, String> setter, String line) {
        String result = extractIdLink(pattern, group, line);
        if (!result.isEmpty()) {
            setter.accept(vkTable, result);
        }
    }

    private static String extractIdLink(String patternStr, int group, String lineWithData) {
        Matcher matcher = Pattern.compile(patternStr).matcher(lineWithData);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return lineWithData;
    }

}
