package com.example.musicplayer.data.api;

public class AuthLoginRequest{
    private String email;
    private String password;

    public AuthLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}

