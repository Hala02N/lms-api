package com.example.exceptions;

public class ErrorResponse {
    private int statusCode;
    private String message;

    public ErrorResponse(int code, String message)
    {
        super();
        this.message = message;
        this.statusCode = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
