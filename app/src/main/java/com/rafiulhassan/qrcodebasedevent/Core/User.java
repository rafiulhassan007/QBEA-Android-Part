package com.rafiulhassan.qrcodebasedevent.Core;

public class User {
    private String uName;
    private String uEmail;
    private String uPhone;
    private String uToken;
    private String uType;
    private String uLoggedIn;
    private String uAddress;

    public User(String uName, String uEmail,String uPhone, String uToken, String uType, String uLoggedIn,String uAddress) {
        this.uName = uName;
        this.uEmail = uEmail;
        this.uPhone = uPhone;
        this.uToken = uToken;
        this.uType = uType;
        this.uLoggedIn = uLoggedIn;
        this.uAddress = uAddress;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuPhone() {
        return uPhone;
    }

    public void setuPhone(String uPhone) {
        this.uPhone = uPhone;
    }

    public String getuToken() {
        return uToken;
    }

    public void setuToken(String uToken) {
        this.uToken = uToken;
    }

    public String getuType() {
        return uType;
    }

    public void setuType(String uType) {
        this.uType = uType;
    }

    public String getuLoggedIn() {
        return uLoggedIn;
    }

    public void setuLoggedIn(String uLoggedIn) {
        this.uLoggedIn = uLoggedIn;
    }

    public String getuAddress() {
        return uAddress;
    }

    public void setuAddress(String uAddress) {
        this.uAddress = uAddress;
    }
}
