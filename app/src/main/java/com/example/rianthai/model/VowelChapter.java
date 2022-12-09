package com.example.rianthai.model;

import java.io.Serializable;

public class VowelChapter extends Chapter implements Serializable {

    String desNote;

    public VowelChapter(String contentName, String contentPron, String contentAudio) {
        super(contentName, contentPron, contentAudio);
    }

    public String getDesNote() {
        return desNote;
    }

    public void setDesNote(String desNote) {
        this.desNote = desNote;
    }
}
