package com.kmv.chatapp.Model;

public class Chatlist {
    public String receiverId,senderId;

    public Chatlist(String receiverId,String senderId) {
        this.receiverId = receiverId;
        this.senderId=senderId;
    }

    public Chatlist() {
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
