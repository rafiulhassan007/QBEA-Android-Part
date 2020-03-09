package com.rafiulhassan.qrcodebasedevent.AttendanceReport;

public class ReportModel {
    private String reportId;
    private String date;

    public ReportModel(String reportId, String date) {
        this.reportId = reportId;
        this.date = date;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
