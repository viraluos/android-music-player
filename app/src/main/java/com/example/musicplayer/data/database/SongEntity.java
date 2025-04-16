package com.example.musicplayer.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class SongEntity {

    @PrimaryKey
    public String id;

    public String title;
    public String author;
    public String prod;
    public String image;
    public String song;
    public int views;
    public String duration;
    public String uploader;

    public SongEntity(String id, String title, String author, String prod, String image,
                      String song, int views, String duration, String uploader) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.prod = prod;
        this.image = image;
        this.song = song;
        this.views = views;
        this.duration = duration;
        this.uploader = uploader;
    }

}