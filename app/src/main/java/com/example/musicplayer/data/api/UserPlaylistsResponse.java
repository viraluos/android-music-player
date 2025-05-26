package com.example.musicplayer.data.api;

import java.util.List;

public class UserPlaylistsResponse {
    private boolean success;
    private List<Playlist> playlists;
    private String error;

    public UserPlaylistsResponse(List<Playlist> playlists) { // sucesso
        this.success = true;
        this.playlists = playlists;
    }

    public UserPlaylistsResponse(String error) { // errore
        this.success = false;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public String getError() {
        return error;
    }
}
