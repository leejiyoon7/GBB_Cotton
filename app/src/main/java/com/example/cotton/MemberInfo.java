package com.example.cotton;

public class MemberInfo {
    private String name;
    private String phoneNumber;
    private String wallet;
    private long ticket;
    private String profileLink;
    private String token;


    public MemberInfo(String name, String phoneNumber, String wallet, long ticket, String profileLink, String token) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.wallet = wallet;
        this.ticket = ticket;
        this.profileLink = profileLink;
        this.token = token;
    }

    public long getTicket() {
        return ticket;
    }

    public void setTicket(long ticket) {
        this.ticket = ticket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }
}

