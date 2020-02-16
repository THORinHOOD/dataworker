package com.thorinhood.dataworker.utils.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class PersonInfo extends HashMap<String, Optional<Object>> {

    public PersonInfo() {
        super();
    }

    public PersonInfo(Map<String, Optional<Object>> map) {
        super(map);
    }

    public Optional<Integer> getInteger(String key) {
        Optional<Object> value = getValueByKey(key)
                .filter(x -> x instanceof Integer || x instanceof String);
        if (value.isEmpty()) {
            return Optional.empty();
        } else if (value.get() instanceof Integer) {
            return Optional.of((Integer) value.get());
        } else {
            return Optional.of(Integer.valueOf((String) value.get()));
        }
    }

    public <FROM, TO> Optional<TO> getAndConvertValue(String key, Class<FROM> clazz, Function<FROM, TO> converter) {
        Optional<Object> value = getValueByKey(key);
        if (value.isEmpty() || !value.get().getClass().equals(clazz)) {
            return Optional.empty();
        }
        return Optional.of(converter.apply((FROM) value.get()));
    }

    public Optional<Long> getLong(String key) {
        Optional<Object> value = getValueByKey(key)
            .filter(x -> x instanceof Long || x instanceof String || x instanceof Integer);
        if (value.isEmpty()) {
            return Optional.empty();
        } else if (value.get() instanceof Long) {
            return Optional.of((Long) value.get());
        } else if (value.get() instanceof Integer) {
            return Optional.of(Long.valueOf((Integer) value.get()));
        } else {
            return Optional.of(Long.valueOf((String) value.get()));
        }
    }

    public String getNString(String key) {
        return getString(key).orElse(null);
    }

    public Optional<String> getString(String key) {
        Optional<Object> value = getValueByKey(key);
        return value.map(String::valueOf);
    }

    public Optional<Object> getValueByKey(String key) {
        return containsKey(key) ? get(key) : Optional.empty();
    }

    public Optional<String> getTwitter() {
        return getString("twitter");
    }

    public Optional<String> getVK() {
        return getString("vk");
    }

    public Optional<String> getFacebook() {
        return getString("facebook");
    }

    public Optional<String> getInstagram() {
        return getString("instagram");
    }

    public Optional<String> getOdnoklassniki() {
        return getString("odnoklassniki");
    }

}
