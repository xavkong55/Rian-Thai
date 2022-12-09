package com.example.rianthai.model;

import java.io.Serializable;

public class ConsonantChapter extends Chapter implements Serializable {

    private String desName, desPron, desMeaning,desImage;

    public ConsonantChapter(String contentName, String contentPron, String contentAudio) {
        super(contentName, contentPron, contentAudio);
    }

    public String getDesImage() {
        return desImage;
    }

    public void setDesImage(String desImage) {
        this.desImage = desImage;
    }

    public String getDesName() {
        return desName;
    }

    public void setDesName(String desName) {
        this.desName = desName;
    }

    public String getDesPron() {
        return desPron;
    }

    public void setDesPron(String desPron) {
        this.desPron = desPron;
    }

    public String getDesMeaning() {
        return desMeaning;
    }

    public void setDesMeaning(String desMeaning) {
        this.desMeaning = desMeaning;
    }

}
