package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class VKFriendsService {
    private static final String vkfaces = "https://vkfaces.com/api/vk-user/friends";

    private final VKDBService vkdbService;
    private RestTemplate restTemplate;

    public VKFriendsService(VKDBService vkdbService) {
        this.vkdbService = vkdbService;
        restTemplate = new RestTemplate();
    }

    public Collection<VKUnindexedTable> getFriends(String id) throws ParseException {
        Collection<VKUnindexedTable> result = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("id", id);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(vkfaces, request , String.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            return Collections.emptyList();
        }

        JSONObject jo = (JSONObject) new JSONParser().parse(response.getBody());
        JSONArray friends = (JSONArray) ((JSONObject) jo.get("response")).get("friends");
        Iterator friendsItr = friends.iterator();
        while (friendsItr.hasNext()) {
            JSONObject friend = (JSONObject) friendsItr.next();
            String domain = (String) friend.get("domain");
            result.add(new VKUnindexedTable(domain));
        }
        return result;
    }

}
