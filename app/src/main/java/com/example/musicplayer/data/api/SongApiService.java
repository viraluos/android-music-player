package com.example.musicplayer.data.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SongApiService {
    @GET("songs.php")
    Call<List<Song>> getAllSongs();

    @GET("songs_from_playlist.php")
    Call<List<Song>> getSongsFromPlaylist(@Query("name") String playlistName);

    @GET("random_songs.php")
    Call<List<Song>> getRandomSongs();

    @GET("playlists.php")
    Call<List<Playlist>> getPlaylists();

}

