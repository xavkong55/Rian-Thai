package com.example.rianthai.model;

public class CommentReport extends Report{

    String commentID;

    public CommentReport(boolean reportMisleading, boolean reportSexual, boolean reportProhibited, boolean reportOffensive, boolean reportOthers, String reportType, String questionID, String reportDate, String userName, String commentID) {
        super(reportMisleading, reportSexual, reportProhibited, reportOffensive, reportOthers, reportType, questionID, reportDate,userName);
        this.commentID = commentID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}
