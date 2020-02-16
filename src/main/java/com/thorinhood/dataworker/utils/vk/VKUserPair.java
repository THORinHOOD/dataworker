package com.thorinhood.dataworker.utils.vk;

import com.thorinhood.dataworker.tables.VKTable;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class VKUserPair<TYPE> {

    private UserField userField;
    private Function<UserXtrCounters, TYPE> extractor;
    private BiConsumer<VKTable, TYPE> setter;
    private String key;

    public static <T> Builder<T> newBuilder(Class<T> clazz) {
        return new Builder<>();
    }

    public VKUserPair(UserField userField, Function<UserXtrCounters, TYPE> extractor,
                      BiConsumer<VKTable, TYPE> setter,  String key) {
        this.userField = userField;
        this.extractor = extractor;
        this.setter = setter;
        this.key = key;
    }

    public String getKey() {
        return key == null ? userField.getValue() : key;
    }

    public VKTable process(VKTable vkTable, UserXtrCounters user) {
        setter.accept(vkTable, extractor.apply(user));
        return vkTable;
    }

    public UserField getUserField() {
        return userField;
    }

    public static class Builder<TYPE> {
        private UserField userField;
        private Function<UserXtrCounters, TYPE> extractor;
        private BiConsumer<VKTable, TYPE> setter;
        private String key;

        public Builder<TYPE> setter(BiConsumer<VKTable, TYPE> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<TYPE> userField(UserField userField) {
            this.userField = userField;
            return this;
        }

        public Builder<TYPE> extractor(Function<UserXtrCounters, TYPE> extractor) {
            this.extractor = extractor;
            return this;
        }

        public Builder<TYPE> key(String key) {
            this.key = key;
            return this;
        }

        public VKUserPair<TYPE> build() {
            return new VKUserPair(userField, extractor, setter, key);
        }

    }

}
