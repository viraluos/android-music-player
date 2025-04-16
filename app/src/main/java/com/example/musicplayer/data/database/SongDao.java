package com.example.musicplayer.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
// import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SongDao {

    @Insert
    void insert(SongEntity song);

    @Update
    void update(SongEntity song);

    @Query("SELECT * FROM songs ORDER BY views DESC")
    List<SongEntity> getAllSongs();

    @Query("SELECT * FROM songs WHERE id = :id")
    SongEntity getSongById(String id);

}