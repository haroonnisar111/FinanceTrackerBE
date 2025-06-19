package com.budgetbuddy.personal_finance_tracker.exception;

public class FileProcessingException extends RuntimeException {

    private final String fileName;
    private final String fileType;
    private final String operation;

    public FileProcessingException(String fileName, String operation, String message) {
        super(String.format("Failed to %s file '%s': %s", operation, fileName, message));
        this.fileName = fileName;
        this.operation = operation;
        this.fileType = null;
    }

    public FileProcessingException(String fileName, String fileType, String operation, String message) {
        super(String.format("Failed to %s %s file '%s': %s", operation, fileType, fileName, message));
        this.fileName = fileName;
        this.fileType = fileType;
        this.operation = operation;
    }

    public FileProcessingException(String message) {
        super(message);
        this.fileName = null;
        this.fileType = null;
        this.operation = null;
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
        this.fileType = null;
        this.operation = null;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getOperation() {
        return operation;
    }
}