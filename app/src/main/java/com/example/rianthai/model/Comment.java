package com.example.rianthai.model;

import java.io.Serializable;

public class Comment implements Serializable {
    String commentDescription, username, user_photo, commentDate;

    public Comment(String commentDescription, String commentDate, String username, String user_photo) {
        this.commentDescription = commentDescription;
        this.commentDate = commentDate;
        this.username = username;
        this.user_photo = user_photo;
    }

    public String getCommentDescription() {
        return commentDescription;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getCommentDate() {
        return commentDate;
    }

}
