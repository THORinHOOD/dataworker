package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class VKFriendsService {
    private static final String pattern = "https://vklist.ru/user/uid%s/friends";
    private static final String vkfaces = "https://vkfaces.com/api/vk-user/friends";

    private final VKDBService vkdbService;
    private RestTemplate restTemplate;

    public VKFriendsService(VKDBService vkdbService) {
        this.vkdbService = vkdbService;
        restTemplate = new RestTemplate();
    }

    public Collection<VKUnindexedTable> getFriends(String id) {
        if (id == null) {
            return Collections.emptyList();
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(String.format(pattern, id), String.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            return Collections.emptyList();
        }
        return parse(response.getBody()).stream()
                .map(Long::valueOf)
                .map(VKUnindexedTable::new)
                .collect(Collectors.toList());
    }

    private Collection<String> parse(String page) {
        Collection<String> friends = new ArrayList<>();
        Document doc = Jsoup.parse(page);
        Elements friendsElements = doc.select(".user-common");
        for (Element element : friendsElements) {
            Elements elements = element.select("a.href");
            String value = element.select("a").first().attr("href");
            if (value != null && value.length() > 9) {
                friends.add(value.substring(9));
            }
        }
        return friends;
    }

}
