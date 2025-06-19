package com.budgetbuddy.personal_finance_tracker.exception;

import java.time.LocalDate;

public class ReportGenerationException extends RuntimeException {

    private final String reportType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String reason;

    public ReportGenerationException(String reportType, String reason) {
        super(String.format("Failed to generate %s report: %s", reportType, reason));
        this.reportType = reportType;
        this.reason = reason;
        this.startDate = null;
        this.endDate = null;
    }

    public ReportGenerationException(String reportType, LocalDate startDate, LocalDate endDate, String reason) {
        super(String.format("Failed to generate %s report for period %s to %s: %s",
                reportType, startDate, endDate, reason));
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public ReportGenerationException(String message) {
        super(message);
        this.reportType = null;
        this.reason = null;
        this.startDate = null;
        this.endDate = null;
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
        this.reportType = null;
        this.reason = null;
        this.startDate = null;
        this.endDate = null;
    }

    public String getReportType() {
        return reportType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }
}
