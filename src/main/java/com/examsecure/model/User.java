package com.examsecure.model;

public class User {

    private int userId;
    private String username;
    private String role;

    public User(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // Existing getter
    public int getUserId() {
        return userId;
    }

    // Compatibility getter used by existing servlets
    public int getId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}