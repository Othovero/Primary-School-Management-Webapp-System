package com.studentinformationmanagementsystem.model;

import java.util.Date;

public class User {
    private int userId;
    private String username;
    private String password;  // Only used for transfer, never stored in plain text
    private String userType;  // ADMIN, TEACHER, PARENT
    private String email;
    private Date createdAt;
    private Date lastLogin;
    private String rememberToken;
    private boolean status;

    // Default constructor
    public User() {
        this.status = true;
        this.createdAt = new Date();
    }

    // Constructor with basic fields
    public User(String username, String email, String userType) {
        this();
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    // Utility methods
    public boolean isAdmin() {
        return "ADMIN".equals(userType);
    }

    public boolean isTeacher() {
        return "TEACHER".equals(userType);
    }

    public boolean isParent() {
        return "PARENT".equals(userType);
    }

    public boolean isActive() {
        return status;
    }

    // Override toString() for better logging and debugging
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", userType='" + userType + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }

    // Override equals() and hashCode() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != user.userId) return false;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + username.hashCode();
        return result;
    }
}