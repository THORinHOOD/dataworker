package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.utils.common.MeasureTimeUtil;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VKFriendsService {
    private static final String vkfaces = "https://vkfaces.com/api/vk-user/friends";

    private final VKDBService vkdbService;
    private RestTemplate restTemplate;

    public VKFriendsService(VKDBService vkdbService) {
        this.vkdbService = vkdbService;
        restTemplate = new RestTemplate();
    }

    public List<String> getFriends(String id) throws ParseException {
        List<String> result = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("id", id);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

//        MeasureTimeUtil measureTimeUtil = new MeasureTimeUtil();
//        measureTimeUtil.start();
        ResponseEntity<String> response = restTemplate.postForEntity(vkfaces, request , String.class);
//        System.out.println(
//                String.format("For user %s get friends took : %d ms", id, measureTimeUtil.resultMilliseconds())
//        );

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            return Collections.emptyList();
        }

        JSONObject jo = (JSONObject) new JSONParser().parse(response.getBody());
        JSONArray friends = (JSONArray) ((JSONObject) jo.get("response")).get("friends");
        Iterator friendsItr = friends.iterator();
        while (friendsItr.hasNext()) {
            JSONObject friend = (JSONObject) friendsItr.next();
            result.add((String) friend.get("domain"));
        }
        return result;
    }

}
