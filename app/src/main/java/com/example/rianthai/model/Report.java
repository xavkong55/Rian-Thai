package com.example.rianthai.model;

public class Report {
    boolean reportMisleading,reportSexual,reportProhibited,reportOffensive,reportOthers;
    String reportType,questionID,reportDate,userName;

    public Report(boolean reportMisleading, boolean reportSexual, boolean reportProhibited, boolean reportOffensive, boolean reportOthers, String reportType, String questionID, String reportDate, String userName) {
        this.reportMisleading = reportMisleading;
        this.reportSexual = reportSexual;
        this.reportProhibited = reportProhibited;
        this.reportOffensive = reportOffensive;
        this.reportOthers = reportOthers;
        this.reportType = reportType;
        this.questionID = questionID;
        this.reportDate = reportDate;
        this.userName = userName;
    }

    public boolean isReportMisleading() {
        return reportMisleading;
    }

    public void setReportMisleading(boolean reportMisleading) {
        this.reportMisleading = reportMisleading;
    }

    public boolean isReportSexual() {
        return reportSexual;
    }

    public void setReportSexual(boolean reportSexual) {
        this.reportSexual = reportSexual;
    }

    public boolean isReportProhibited() {
        return reportProhibited;
    }

    public void setReportProhibited(boolean reportProhibited) {
        this.reportProhibited = reportProhibited;
    }

    public boolean isReportOffensive() {
        return reportOffensive;
    }

    public void setReportOffensive(boolean reportOffensive) {
        this.reportOffensive = reportOffensive;
    }

    public boolean isReportOthers() {
        return reportOthers;
    }

    public void setReportOthers(boolean reportOthers) {
        this.reportOthers = reportOthers;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReportType() {
        return reportType;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getReportDate() {
        return reportDate;
    }
}
