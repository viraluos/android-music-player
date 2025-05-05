package com.example.musicplayer.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.data.api.ApiClient;
import com.example.musicplayer.data.api.SongApiService;
import com.example.musicplayer.data.api.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongListActivity extends AppCompatActivity {

    private LinearLayout songsContainer;
    private ProgressBar progressBar;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songsContainer = findViewById(R.id.songs_container);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorText);

        loadSongs();
    }

    private void loadSongs() {
        showLoading();

        SongApiService apiService = ApiClient.getClient().create(SongApiService.class);
        apiService.getAllSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    displaySongs(response.body());
                } else {
                    showError("Errore nel caricamento: " + response.code());
                    Log.e("API_ERROR", "Response error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                hideLoading();
                showError("Errore di connessione: " + t.getMessage());
                Log.e("API_ERROR", "Network error: ", t);
            }
        });
    }

    private void displaySongs(List<Song> songs) {
        songsContainer.removeAllViews();

        if (songs.isEmpty()) {
            showEmptyMessage();
            return;
        }

        for (Song song : songs) {
            View songView = createSongView(song);
            songsContainer.addView(songView);
        }
    }

    private View createSongView(Song song) {
        View view = getLayoutInflater().inflate(R.layout.block_song, null);

        TextView titleView = view.findViewById(R.id.songTitle);
        TextView authorView = view.findViewById(R.id.songAuthor);

        titleView.setText(song.getTitle());
        authorView.setText(song.getAuthor());

        view.setOnClickListener(v -> {
            Toast.makeText(this, "Avvio riproduzione: " + song.getTitle(), Toast.LENGTH_SHORT).show();
            // Implementa la logica di riproduzione qui
            // playSong(song.getSongPath());
        });

        return view;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        songsContainer.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        songsContainer.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        errorTextView.setText(message + "\n\nTocca per riprovare");
        errorTextView.setOnClickListener(v -> {
            errorTextView.setVisibility(View.GONE);
            loadSongs();
        });
        errorTextView.setText(message);
        errorTextView.setTextColor(Color.RED);
        errorTextView.setVisibility(View.VISIBLE);
        songsContainer.setVisibility(View.GONE);
    }

    private void showEmptyMessage() {
        errorTextView.setText("Nessuna canzone disponibile");
        errorTextView.setTextColor(Color.BLACK);
        errorTextView.setVisibility(View.VISIBLE);
    }
}