package com.thorinhood.dataworker.services;

import com.thorinhood.dataworker.configs.VKConfiguration;
import com.thorinhood.dataworker.services.db.DBService;
import com.thorinhood.dataworker.services.db.VKDBService;
import com.thorinhood.dataworker.services.parser.VKParser;
import com.thorinhood.dataworker.utils.common.PersonInfo;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Import({VKConfiguration.class})
public class CommonService {

    private final TwitterService twitterService;
    private final VKService vkService;
//    private final FacebookService facebookService;

    public CommonService(TwitterService twitterService,
                         VKService vkService,
                         VKDBService vkdbService) {
                         //FacebookService facebookService,
                         //DBService dbService) {
        this.vkService = vkService;
        this.twitterService = twitterService;

        VKParser vkParser = new VKParser(vkdbService);
        vkParser.getFriends(135336811L);
    //    this.facebookService = facebookService;

       // twitterService.getTwitter().searchOperations().search("q");
//        int a = 5;


//        twitterService.getDefaultUsersInfo(Collections.singletonList("k160rg"));
//        Collection<String> userVKIds = List.of(
//            "135336811",
//            "53636214",
//            "210700286",
//            "172252308",
//            "218719153",
//            "151403319"
//        );
//
//        Collection<VKTable> vkResult = vkService.getDefaultUsersInfo(userVKIds);
//        Collection<String> twitterNames = extractTwitterNamesFromVk(vkResult);
//        List<PersonInfo> twitterResult = twitterService.getDefaultUsersInfo(twitterNames);
//        System.out.println(twitterResult);
//        facebook -> {Optional@7637} "Optional[∙ Публичная страница FaceBook: http://www.facebook.com/]"
//        facebookService.getDefaultUsersInfo(List.of(
//                "thorinhood"
//            "774387969744085"
//                "4"
//            "774387969744085"
//        ));


//        dbService.saveVK(vkResult);
//        Collection<TwitterTable> twitterResult = twitterService.getDefaultUsersInfo(vkResult.stream()
//                        .map(VKTable::getTwitter)
//                        .collect(Collectors.toList()));
//        Collection<String> twitterScreenNames = List.of(
//                "EeOneGuy",
//                "RealVolya",
//                "yan_gordienko",
//                "LindseyStirling",
//                "k160rg"
//        );
//        dbService.saveTwitter(twitterService.getDefaultUsersInfo(twitterScreenNames));
//        dbService.testCassandraTemplate("hello");
    }

    private Collection<String> extractTwitterNamesFromVk(List<PersonInfo> vkProfiles) {
        return vkProfiles.stream()
            .map(PersonInfo::getTwitter)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    }

}
