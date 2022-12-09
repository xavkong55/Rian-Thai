package com.example.rianthai.model;

import java.io.Serializable;

public class Chapter implements Serializable {
    private final String contentName;
    private final String contentPron;
    private final String contentAudio;

    public Chapter(String contentName, String contentPron, String contentAudio) {
        this.contentName = contentName;
        this.contentPron = contentPron;
        this.contentAudio = contentAudio;
    }

    public String getContentName() {
        return contentName;
    }

    public String getContentPron() {
        return contentPron;
    }

    public String getContentAudio() {
        return contentAudio;
    }


}
