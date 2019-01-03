package com.chat3j.android;

import java.util.Date;

public class ChatItem {
    private String Message;
    private String UserName;
    private java.util.Date Date;
    private Sender Sender;

    public ChatItem(String UserName, String Message) {
        this.Message = Message;
        this.Sender = Sender.OTHER;
        this.UserName = UserName;
        this.Date = new Date();
    }

    public ChatItem(String Message) {
        this.Message = Message;
        this.Sender = Sender.ME;
        this.Date = new Date();
    }

    public java.util.Date getDate() {
        return Date;
    }

    public String getMessage() {
        return Message;
    }

    public String getUserName() {
        return UserName;
    }

    public ChatItem.Sender getSender() {
        return Sender;
    }

    public enum Sender {
        ME, OTHER;
    }
}
