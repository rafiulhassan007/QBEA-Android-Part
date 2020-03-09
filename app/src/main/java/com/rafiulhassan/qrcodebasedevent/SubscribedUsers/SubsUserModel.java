package com.rafiulhassan.qrcodebasedevent.SubscribedUsers;

public class SubsUserModel {
    private String userId;
    private String userName;
    private String userEmail;
    private String userAccepted;

    public SubsUserModel(String userId, String userName, String userEmail, String userAccepted) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAccepted = userAccepted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAccepted() {
        return userAccepted;
    }

    public void setUserAccepted(String userAccepted) {
        this.userAccepted = userAccepted;
    }
}
