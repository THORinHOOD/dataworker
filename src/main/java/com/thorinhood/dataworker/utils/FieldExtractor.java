package com.thorinhood.dataworker.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FieldExtractor<FROM, TO, TYPE> {

    private Function<FROM, TYPE> extractor;
    private BiConsumer<TO, TYPE> setter;
    private String key;
    private Map<String, Object> additional = new HashMap<>();

    public static <FROM, TO, TYPE> Builder<FROM, TO, TYPE> newBuilder() {
        return new Builder<>();
    }

    public FieldExtractor(String key,
                          Function<FROM, TYPE> extractor,
                          BiConsumer<TO, TYPE> setter) {
        this.extractor = extractor;
        this.setter = setter;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public TO process(FROM from, TO to) {
        setter.accept(to, extractor.apply(from));
        return to;
    }

    public boolean containsAdditional(String key) {
        return additional.containsKey(key);
    }

    public void addAdditional(String key, Object value) {
        additional.put(key, value);
    }

    public Object getAdditional(String key) {
        return additional.get(key);
    }

    public static class Builder<FROM, TO, TYPE> {
        private Function<FROM, TYPE> extractor;
        private BiConsumer<TO, TYPE> setter;
        private String key;

        public Builder<FROM, TO, TYPE> setExtractor(Function<FROM, TYPE> extractor) {
            this.extractor = extractor;
            return this;
        }

        public Builder<FROM, TO, TYPE> setSetter(BiConsumer<TO, TYPE> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<FROM, TO, TYPE> setKey(String key) {
            this.key = key;
            return this;
        }

        public FieldExtractor<FROM, TO, TYPE> build() {
            return new FieldExtractor<>(key, extractor, setter);
        }

    }

}
