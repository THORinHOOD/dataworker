package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.tables.VKTable;
import com.thorinhood.dataworker.tables.VKUnindexedTable;
import com.thorinhood.dataworker.utils.common.FieldExtractor;
import com.thorinhood.dataworker.utils.vk.VKDataUtil;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;
import com.vk.api.sdk.objects.friends.responses.GetResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.users.UsersNameCase;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VKService implements SocialService<VKTable> {

    private static final String USER_FIELD = "userField";
    private TransportClient transportClient;
    private VkApiClient vk;
    private ServiceClientCredentialsFlowResponse authResponse;
    private ServiceActor serviceActor;
    private VKDBService dbService;
    private VKFriendsService vkFriendsService;

    public VKService(String vkServiceAccessKey,
                     String vkClientSecret,
                     Integer vkAppId,
                     VKDBService dbService,
                     VKFriendsService vkFriendsService) throws ClientException, ApiException {
        this.dbService = dbService;
        this.vkFriendsService = vkFriendsService;
        transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        authResponse = vk.oauth()
                .serviceClientCredentialsFlow(vkAppId, vkClientSecret)
                .execute();
        serviceActor = new ServiceActor(vkAppId, vkClientSecret, vkServiceAccessKey);
    }

    public VkApiClient getVk() {
        return vk;
    }

    public Collection<VKTable> getDefaultUsersInfo(Collection<String> userIds) {
        List<FieldExtractor> pairs = List.of(
                pair(UserField.DOMAIN, UserXtrCounters::getDomain, VKTable::setDomain),
                pair("id", UserXtrCounters::getId, (vk, x) -> vk.setId(Long.valueOf(x))),
                pair(UserField.ABOUT, UserXtrCounters::getAbout, VKTable::setAbout),
                pair(UserField.PHOTO_50, UserXtrCounters::getPhoto50, VKTable::setPhoto50),
                pair(UserField.SEX, x -> {
                    if (x.getSex() != null) {
                        return x.getSex().name();
                    }
                    return "";
                }, (vk, sex) -> {
                    if (sex.equalsIgnoreCase("MALE")) {
                        vk.setMale(true);
                        vk.setFemale(false);
                        vk.setUnknownSex(false);
                    } else if (sex.equalsIgnoreCase("FEMALE")) {
                        vk.setFemale(true);
                        vk.setMale(false);
                        vk.setUnknownSex(false);
                    } else {
                        vk.setFemale(false);
                        vk.setMale(false);
                        vk.setUnknownSex(true);
                    }
                }),
                pair(UserField.BOOKS, UserXtrCounters::getBooks, VKTable::setBooks),
                pair(UserField.CITY, x -> {
                    if (x.getCity() != null) {
                        return x.getCity().getTitle();
                    }
                    return null;
                }, VKTable::setCity),
                pair("firstname", UserXtrCounters::getFirstName, VKTable::setFirstName),
                pair("lastname", UserXtrCounters::getLastName, VKTable::setLastName),
                pair(UserField.NICKNAME, UserXtrCounters::getNickname, VKTable::setNickname),
               // pair(UserField.PERSONAL, x -> x.getPersonal().),
                pair(UserField.EDUCATION, UserXtrCounters::getEducationForm, VKTable::setEducation),
                pair(UserField.BDATE, UserXtrCounters::getBdate, VKTable::setBdate),
                pair(UserField.COUNTRY, x -> {
                    if (x.getCountry() != null) {
                        return x.getCountry().getTitle();
                    }
                    return null;
                }, VKTable::setCountry),
                pair("instagram", UserXtrCounters::getInstagram, VKTable::setInstagram),
                pair("facebook", UserXtrCounters::getFacebook, VKTable::setFacebook),
                pair("twitter", UserXtrCounters::getTwitter, VKTable::setTwitter)
               // pair(UserField.CONTACTS, user -> Optional.empty())
        );

        try {
            return getUsersInfo(
                pairs,
                Collections.singletonList(UserField.CONNECTIONS),
                UsersNameCase.NOMINATIVE,
                0,
                userIds.toArray(new String[0])
            );
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Collection<VKTable> getUsersInfo(Collection<FieldExtractor> pairs,
                                      Collection<UserField> extra,
                                      UsersNameCase nameCase,
                                      Integer depth,
                                      String... userIds) throws ClientException, ApiException {
        List<UserField> fields = pairs.stream()
                .filter(pair -> pair.containsAdditional(USER_FIELD))
                .map(pair -> (UserField) pair.getAdditional(USER_FIELD))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        fields.addAll(extra);

        List<UserXtrCounters> users = vk.users().get(serviceActor)
                .fields(fields)
                .nameCase(nameCase)
                .userIds(userIds)
                .execute();

        Map<Long, VKTable> result = users.stream()
                .filter(Objects::nonNull)
                .map(user -> {
                    VKTable vkTable = new VKTable();
                    pairs.stream()
                        .filter(Objects::nonNull)
                        .forEach(pair -> pair.process(user, vkTable));
                    return vkTable;
                })
                .collect(Collectors.toMap(VKTable::getId, Function.identity()));

        result.values().forEach(VKDataUtil::extractLinks);

        for (VKTable vkTable : result.values()) {
            Collection<VKUnindexedTable> friends = null;
            try {
                friends = vkFriendsService.getFriends(String.valueOf(vkTable.getId()));
                dbService.saveUnindexed(friends);
            } catch(Exception exception) {
                exception.printStackTrace();
            }
            if (depth > 0 && friends != null && friends.size() != 0) {
                result.putAll(getUsersInfo(pairs, extra, nameCase, depth - 1, friends.stream().map(VKUnindexedTable::getId).toArray(String[]::new))
                    .stream()
                    .collect(Collectors.toMap(VKTable::getId, Function.identity())));
            }
        }



//        for (String id : userIds) {
//            result.get(Integer.valueOf(id)).setFriends(getUsersFriends(nameCase, Integer.valueOf(id)));
//        }

      /*  List<VKTable> resultAll = new ArrayList<>(result.values());
        if (depth > 0) {
            for (VKTable vkTable : result.values()) {
                resultAll.addAll(getUsersInfo(pairs, extra, nameCase, depth - 1, vkTable.getFriends().stream()
                    .map(String::valueOf)
                    .toArray(String[]::new)));
            }
        }*/

        return result.values();
    }

    private List<Integer> getUsersFriends(UsersNameCase nameCase, Integer userId) throws ClientException, ApiException {
        GetResponse getResponse;
        try {
            getResponse = vk.friends().get(serviceActor)
                    .nameCase(nameCase)
                    .userId(userId)
                    .execute();
        } catch(Exception exception) {
            exception.printStackTrace();
            return Collections.emptyList();
        }

        if (getResponse.getItems() != null) {
            return getResponse.getItems();
        }
        return Collections.emptyList();
    }

    private <TYPE> FieldExtractor<UserXtrCounters, VKTable, TYPE> pair(String key,
                                             Function<UserXtrCounters, TYPE> extractor,
                                             BiConsumer<VKTable, TYPE> setter) {
        return FieldExtractor.<UserXtrCounters, VKTable, TYPE>newBuilder()
                .setKey(key)
                .setExtractor(extractor)
                .setSetter(setter)
                .build();
    }

    private <TYPE> FieldExtractor<UserXtrCounters, VKTable, TYPE> pair(UserField userField,
                                         Function<UserXtrCounters, TYPE> extractor,
                                         BiConsumer<VKTable, TYPE> setter) {
        FieldExtractor<UserXtrCounters, VKTable, TYPE> ext = pair(userField.getValue(), extractor, setter);
        ext.addAdditional(USER_FIELD, userField);
        return ext;
    }

}
