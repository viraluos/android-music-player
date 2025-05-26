package com.example.musicplayer.data.api;

public class AddSongResponse {
    private boolean success;
    private String message;

    // Costruttore
    public AddSongResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getter
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}