package com.example.rianthai.model;

import java.io.Serializable;
import java.util.ArrayList;

public class VocabGrammarChapter extends Chapter implements Serializable {

    String contentMeaning;
    ArrayList<String> exampleName, examplePron, exampleMeaning, exampleAudio, exampleNote;

    public VocabGrammarChapter(String contentName, String contentPron, String contentAudio, String contentMeaning) {
        super(contentName, contentPron, contentAudio);
        this.contentMeaning = contentMeaning;
    }

    public String getContentMeaning() {
        return contentMeaning;
    }

    public ArrayList<String> getExampleAudio() {
        return exampleAudio;
    }

    public void setExampleAudio(ArrayList<String> exampleAudio) {
        this.exampleAudio = exampleAudio;
    }

    public ArrayList<String> getExampleName() {
        return exampleName;
    }

    public void setExampleName(ArrayList<String> exampleName) {
        this.exampleName = exampleName;
    }

    public ArrayList<String> getExamplePron() {
        return examplePron;
    }

    public void setExamplePron(ArrayList<String> examplePron) {
        this.examplePron = examplePron;
    }

    public ArrayList<String> getExampleMeaning() {
        return exampleMeaning;
    }

    public void setExampleMeaning(ArrayList<String> exampleMeaning) {
        this.exampleMeaning = exampleMeaning;
    }

    public ArrayList<String> getExampleNote() {
        return exampleNote;
    }

    public void setExampleNote(ArrayList<String> exampleNote) {
        this.exampleNote = exampleNote;
    }
}
