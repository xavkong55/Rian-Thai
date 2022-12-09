package com.example.rianthai.model;

import java.io.Serializable;

public class Question implements Serializable {

    String questionImage, questionDescription, questionTitle, username, questionDate, userPhoto;

    public Question(String questionImage, String questionTitle, String questionDescription, String questionDate, String username, String userPhoto) {
        this.questionImage = questionImage;
        this.questionTitle = questionTitle;
        this.questionDescription = questionDescription;
        this.questionDate = questionDate;
        this.username = username;
        this.userPhoto = userPhoto;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQuestionDate() {
        return questionDate;
    }

    public void setQuestionDate(String questionDate) {
        this.questionDate = questionDate;
    }
}
