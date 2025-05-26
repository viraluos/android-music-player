package com.example.musicplayer.data.api;

public class AddSongRequest {
    private String playlistId;
    private String songId;

    public AddSongRequest(String playlistId, String songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getSongId() {
        return songId;
    }

    public String toJson() {
        return "{\"playlist_id\":\"" + playlistId + "\",\"song_id\":\"" + songId + "\"}";
    }
}