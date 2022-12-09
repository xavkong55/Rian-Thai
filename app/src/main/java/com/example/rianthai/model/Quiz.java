package com.example.rianthai.model;

import java.io.Serializable;

public class Quiz implements Serializable {
    private String question;
    private String selection1;
    private String selection2;
    private String selection3;
    private String selection4;
    private String answer;

    public Quiz(String question, String selection1, String selection2, String selection3, String selection4, String answer) {
        this.question = question;
        this.selection1 = selection1;
        this.selection2 = selection2;
        this.selection3 = selection3;
        this.selection4 = selection4;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSelection1() {
        return selection1;
    }

    public void setSelection1(String selection1) {
        this.selection1 = selection1;
    }

    public String getSelection2() {
        return selection2;
    }

    public void setSelection2(String selection2) {
        this.selection2 = selection2;
    }

    public String getSelection3() {
        return selection3;
    }

    public void setSelection3(String selection3) {
        this.selection3 = selection3;
    }

    public String getSelection4() {
        return selection4;
    }

    public void setSelection4(String selection4) {
        this.selection4 = selection4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
