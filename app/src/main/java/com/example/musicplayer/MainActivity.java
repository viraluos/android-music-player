package com.example.musicplayer;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.data.api.ApiClient;
import com.example.musicplayer.data.api.SongApi;
import com.example.musicplayer.data.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SongApi api = ApiClient.getClient().create(SongApi.class);
        api.getAllSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    for (Song song : response.body()) {
                        Log.d("DEBUG_SONG", song.title + " - " + song.author);
                    }
                } else {
                    Log.e("DEBUG_SONG", "Errore HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("DEBUG_SONG", "Errore di rete: " + t.getMessage());
            }
        });

    }
}
