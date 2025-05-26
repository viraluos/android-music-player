package com.example.musicplayer.data.api;

public class PlaylistResponse {
    private boolean success;
    private String message;
    private String playlistId;

    public PlaylistResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getter e Setter
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}