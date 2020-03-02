package com.thorinhood.dataworker.services.parser;

import com.thorinhood.dataworker.services.db.VKDBService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Collection;
import java.util.Collections;

public class VKParser {

    private static final String URL = "https://vk.com/friends?id=%d&section=all";

    private final VKDBService vkdbService;

    public VKParser(VKDBService vkdbService) {
        this.vkdbService = vkdbService;
    }

    public Collection<Long> getFriends(Long id) {
        try {
            Document doc = Jsoup.connect(String.format(URL, id)).get();

            int a = 5;
            return null;
        } catch (Exception e) {
            System.out.println(String.format("Can't get friends of user %d", id));
            return Collections.emptyList();
        }
    }

}
