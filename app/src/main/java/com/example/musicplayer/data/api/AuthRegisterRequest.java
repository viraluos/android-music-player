package com.example.musicplayer.data.api;

public class AuthRegisterRequest {
    String username;
    private String email;
    private String password;

    public AuthRegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}