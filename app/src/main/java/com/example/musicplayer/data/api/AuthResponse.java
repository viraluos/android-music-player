package com.example.musicplayer.data.api;

import com.example.musicplayer.ui.AccountActivity;

public class AuthResponse {
    public boolean success;
    public String token;
    public String message;

    public boolean isSuccessful() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public String getToken() {
        return token;
    }
}
