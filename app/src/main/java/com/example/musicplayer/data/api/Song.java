package com.example.musicplayer.data.api;

import com.google.gson.annotations.SerializedName;

public class Song {
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("author")
    public String author;

    @SerializedName("image")
    public String image;

    @SerializedName("song_path")
    public String song_path;

    @SerializedName("duration")
    public static String duration;
    public static Song currentSong;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    // Getter e Setter per author
    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    // Getter e Setter per image
    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    // Getter e Setter per song_path
    public String getSongPath() { return song_path; }

    public void setSongPath(String song_path) { this.song_path = song_path; }

    // Getter e Setter per duration
    public static String getDuration() { return duration; }

    public void setDuration(String duration) { Song.duration = duration; }

    public static Song getCurrentSong(){ return currentSong; }

    public static void setCurrentSong(Song currentSong){ Song.currentSong = currentSong; }

}