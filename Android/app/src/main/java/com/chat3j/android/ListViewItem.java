package com.chat3j.android;

public class ListViewItem {
    private String topic;
    private boolean isCreated = false;
    private MODE mode;

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    public enum MODE {
        VOICE, CHAT;
    }
}
