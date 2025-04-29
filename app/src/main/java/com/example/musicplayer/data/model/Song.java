// Song.java
package com.example.musicplayer.data.model;

public class Song {
    private String id;
    private String title;
    private String author;
    private String songPath;

    public Song(){}

    public Song(String title, String author, String songPath) {
        this.title = title;
        this.author = author;
        this.songPath = songPath;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getSongPath() { return songPath; }
    public void setSongPath(String songPath) { this.songPath = songPath; }
}