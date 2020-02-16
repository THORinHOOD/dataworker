package com.thorinhood.dataworker.utils.twitter;

import com.thorinhood.dataworker.utils.common.Extractor;
import org.springframework.social.twitter.api.TwitterProfile;

import java.util.Optional;

public class TwitterUserPair {

    private String key;
    private Extractor<TwitterProfile> extractor;

    public static Builder newBuilder() {
        return new Builder();
    }

    public TwitterUserPair(String key, Extractor<TwitterProfile> extractor) {
        this.key = key;
        this.extractor = extractor;
    }

    public Optional<Object> extract(TwitterProfile profile) {
        return Optional.ofNullable(extractor.apply(profile));
    }

    public String getKey() {
        return key;
    }

    public Extractor<TwitterProfile> getExtractor() {
        return extractor;
    }

    public static class Builder {
        private String key;
        private Extractor<TwitterProfile> extractor;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder extractor(Extractor<TwitterProfile> extractor) {
            this.extractor = extractor;
            return this;
        }

        public TwitterUserPair build() {
            return new TwitterUserPair(key, extractor);
        }

    }

}
