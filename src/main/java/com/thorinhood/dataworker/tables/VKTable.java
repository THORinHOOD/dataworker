package com.thorinhood.dataworker.tables;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table("vk")
public class VKTable implements Profile<Long> {

    @PrimaryKey
    private Long id;

    @Column
    private String about;

    @Column
    private String photo50;

    @Column
    private Boolean male;

    @Column
    private Boolean female;

    @Column
    private Boolean unknownSex;

    @Column
    private String books;

    @Column
    private String city;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String nickname;

    @Column
    private String education;

    @Column
    private String bdate;

    @Column
    private String country;

    @Transient
    private String facebook;

    @Transient
    private String twitter;

    @Transient
    private String instagram;

    @Column
    private List<String> friends;

    @Column
    private String domain;

    public VKTable setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public VKTable setFriends(List<String> friends) {
        this.friends = friends;
        return this;
    }

    public List<String> getFriends() {
        return friends;
    }

    public VKTable setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getAbout() {
        return about;
    }

    public String getPhoto50() {
        return photo50;
    }

    public Boolean getMale() {
        return male;
    }

    public Boolean getFemale() {
        return female;
    }

    public Boolean getUnknownSex() {
        return unknownSex;
    }

    public String getBooks() {
        return books;
    }

    public String getCity() {
        return city;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEducation() {
        return education;
    }

    public String getBdate() {
        return bdate;
    }

    public String getCountry() {
        return country;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setPhoto50(String photo50) {
        this.photo50 = photo50;
    }

    public VKTable setMale(Boolean male) {
        this.male = male;
        return this;
    }

    public VKTable setFemale(Boolean female) {
        this.female = female;
        return this;
    }

    public VKTable setUnknownSex(Boolean unknownSex) {
        this.unknownSex = unknownSex;
        return this;
    }

    public VKTable setBooks(String books) {
        this.books = books;
        return this;
    }

    public VKTable setCity(String city) {
        this.city = city;
        return this;
    }

    public VKTable setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public VKTable setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public VKTable setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public VKTable setEducation(String education) {
        this.education = education;
        return this;
    }

    public VKTable setBdate(String bdate) {
        this.bdate = bdate;
        return this;
    }

    public VKTable setCountry(String country) {
        this.country = country;
        return this;
    }

    public VKTable setFacebook(String facebook) {
        this.facebook = facebook;
        return this;
    }

    public VKTable setTwitter(String twitter) {
        this.twitter = twitter;
        return this;
    }

    public VKTable setInstagram(String instagram) {
        this.instagram = instagram;
        return this;
    }

    @Override
    public Long id() {
        return getId();
    }

    @Override
    public Long vkId() {
        return id;
    }

    @Override
    public String vkDomain() {
        return domain;
    }

    @Override
    public String twitter() {
        return twitter;
    }

    @Override
    public String instagram() {
        return instagram;
    }

    @Override
    public String facebook() {
        return facebook;
    }
}
