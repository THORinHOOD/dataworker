package com.thorinhood.dataworker.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacebookConfiguration {

    @Value("${facebook.app.secret}")
    private String appSecret;

    @Value("${facebook.app.id}")
    private String appId;

    @Value("${facebook.access.token}")
    private String accessToken;

 //   @Bean
//    public FacebookTemplate facebookTemplate() {
//        FacebookTemplate facebookTemplate = new FacebookTemplate(accessToken, null, appId, appSecret);
//        facebookTemplate.setApiVersion("5.0");
//        return facebookTemplate;
  //      return null;
 //   }
//
//    @Bean
//    public FacebookService facebookService(Facebook facebook) {
//        return new FacebookService(facebook);
//    }

}
