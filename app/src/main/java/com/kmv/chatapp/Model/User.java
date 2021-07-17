package com.kmv.chatapp.Model;

public class User {
    private String uid,username,phoneNumber,profileImage,status,search;

    public User(){

    }
    public User(String uid, String username, String phoneNumber, String profileImage,String status,String search) {
        this.uid = uid;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.status = status;
        this.search = search;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
