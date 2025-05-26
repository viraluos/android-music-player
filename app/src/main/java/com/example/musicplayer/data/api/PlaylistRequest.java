package com.example.musicplayer.data.api;

public class PlaylistRequest {
    private String name;

    // Costruttore
    public PlaylistRequest(String name) {
        this.name = name;
    }

    // Getter e Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toJson() {
        return "{\"name\":\"" + name + "\"}";
    }
}
