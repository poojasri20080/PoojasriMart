package com.poojamart.dto;

import com.poojamart.model.User;

public class AuthResponse {

    private boolean success;
    private String message;
    private String token;
    private User user;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message, String token, User user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public static AuthResponse success(String message, String token, User user) {
        return new AuthResponse(true, message, token, user);
    }

    public static AuthResponse failure(String message) {
        return new AuthResponse(false, message, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
