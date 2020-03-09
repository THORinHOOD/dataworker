package com.thorinhood.dataworker.services;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLProtocolException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VKFriendsService {
    private static final Logger logger = LoggerFactory.getLogger(VKFriendsService.class);
    private static final String vkfaces = "https://vkfaces.com/api/vk-user/friends";

    private RestTemplate restTemplate;
    private ExecutorService executorService;

    public VKFriendsService(int threadsCount) {
        restTemplate = new RestTemplate();
        executorService = Executors.newFixedThreadPool(threadsCount);
    }

    public Map<String, List<String>> getFriends(List<String> ids) {
        logger.info("Start loading friends : " + ids.size());
        Set<String> sslErrors = new HashSet<>();
        List<Future<Pair<String, List<String>>>> futures = ids.stream()
              //  .distinct()
                .map(x -> executorService.submit(() -> getFriends(x)))
                .collect(Collectors.toList());
        Map<String, List<String>> result = futures.stream()
                .map(future -> {
                    Pair<String, List<String>> friends = Pair.of("", Collections.emptyList());
                    try {
                        friends = future.get();
                    } catch (Exception exception) {
                        logger.error("While getting future friend", exception);
                    }
                    return friends;
                })
                .filter(x -> CollectionUtils.isNotEmpty(x.getSecond()))
                .distinct()
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        logger.info("Ended loading friends : " + ids.size());
        return result;
    }

    public Pair<String, List<String>> getFriends(String id) throws ParseException, InterruptedException {
        List<String> result = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("id", id);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

//        MeasureTimeUtil measureTimeUtil = new MeasureTimeUtil();
//        measureTimeUtil.start();
        ResponseEntity<String> response = null;
        boolean gotcha = false;
        while (!gotcha) {
            Thread.sleep(100L);
            try {
                response = restTemplate.postForEntity(vkfaces, request, String.class);
                gotcha = true;
            } catch (Exception exception) {
                if (!(exception instanceof SocketException)) {
                    logger.error("Can't get friends of " + id, exception);
                    gotcha = true;
                } else {
                    logger.error("Restart loading friends for" + id);
                }
            }
        }

//        System.out.println(
//                String.format("For user %s get friends took : %d ms", id, measureTimeUtil.resultMilliseconds())
//        );

        if (response == null || !response.getStatusCode().equals(HttpStatus.OK)) {
            return Pair.of(id, Collections.emptyList());
        }

        JSONObject jo = (JSONObject) new JSONParser().parse(response.getBody());
        JSONArray friends = (JSONArray) ((JSONObject) jo.get("response")).get("friends");
        Iterator friendsItr = friends.iterator();
        while (friendsItr.hasNext()) {
            JSONObject friend = (JSONObject) friendsItr.next();
            result.add((String) friend.get("domain"));
        }
        return Pair.of(id, result);
    }

}
