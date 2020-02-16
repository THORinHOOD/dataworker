package com.thorinhood.dataworker.utils.vk;

import com.thorinhood.dataworker.tables.VKTable;

public class VKSimpleInfoExtractor {

    private final static String TWITTER = "://twitter.com/";
    private final static String FACEBOOK = "://www.facebook.com/";

    public static void extractLinks(VKTable vkTable) {
        String about;
        if (vkTable.getAbout() == null || vkTable.getAbout().isEmpty()) {
            return;
        }
        about = vkTable.getAbout();
        String[] lines = about.split("\n");
        for (String line : lines) {
            if (line.contains(TWITTER)) {
                vkTable.setTwitter(line);
            } else if (line.contains(FACEBOOK)) {
                vkTable.setFacebook(line);
            }
        }
    }

}
