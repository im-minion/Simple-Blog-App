package com.example.vaibhav.simpleblogapp.Models;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String profileUrl;
    private long messageTime;

    public ChatMessage() {

    }

    public ChatMessage(String messageText, String messageUser, String profileUrl) {
        this.messageText = messageText;
        this.profileUrl = profileUrl;
        this.messageUser = messageUser;
        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
