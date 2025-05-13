package com.example.musicplayer.ui;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.ImageLoader;
import com.example.musicplayer.R;
import com.example.musicplayer.data.api.ApiClient;
import com.example.musicplayer.data.api.SongApiService;
import com.example.musicplayer.data.api.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongListActivity extends AppCompatActivity {

    private RecyclerView songsRecyclerView;
    private TextView errorTextView;
    private TextView playlistNameText;
    private SongAdapter songAdapter;
    private List<Song> songList = new ArrayList<>();

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songsRecyclerView = findViewById(R.id.songsContainer);
        errorTextView = findViewById(R.id.errorText);
        playlistNameText = findViewById(R.id.playlistName);

        Bundle bundle = getIntent().getExtras();
        String playlistNameFromMainActivity = bundle.getString("playlistNameBundle");

        playlistNameText.setText(playlistNameFromMainActivity);

        mp = new MediaPlayer();

        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SongAdapter(songList);
        songsRecyclerView.setAdapter(songAdapter);

        loadSongs();
    }

    private void loadSongs() {
        showLoading();

        SongApiService apiService = ApiClient.getClient(getApplicationContext()).create(SongApiService.class);
        apiService.getAllSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    songList.clear();
                    songList.addAll(response.body());
                    songAdapter.notifyDataSetChanged();

                    if (songList.isEmpty()) {
                        showEmptyMessage();
                    }
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

    private void showLoading() {
        songsRecyclerView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        songsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(message + "\n\nTocca per riprovare");
        errorTextView.setOnClickListener(v -> {
            errorTextView.setVisibility(View.GONE);
            loadSongs();
        });
        errorTextView.setTextColor(Color.RED);
        songsRecyclerView.setVisibility(View.GONE);
    }

    private void showEmptyMessage() {
        errorTextView.setText("Nessuna canzone disponibile");
        errorTextView.setTextColor(Color.BLACK);
        errorTextView.setVisibility(View.VISIBLE);
        songsRecyclerView.setVisibility(View.GONE);
    }

    // Song Adapter class
    private class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
        private List<Song> songs;

        public SongAdapter(List<Song> songs) {
            this.songs = songs;
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.block_song, parent, false);
            return new SongViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            Song song = songs.get(position);
            holder.bind(song);
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        class SongViewHolder extends RecyclerView.ViewHolder {
            private TextView titleView;
            private TextView authorView;
            private ImageView imageView;

            public SongViewHolder(@NonNull View itemView) {
                super(itemView);
                titleView = itemView.findViewById(R.id.songTitle);
                authorView = itemView.findViewById(R.id.songAuthor);
                imageView = itemView.findViewById(R.id.songImage);
            }

            public void bind(Song song) {
                titleView.setText(song.getTitle());
                authorView.setText(song.getAuthor());

                if (song.getImage() != null && !song.getImage().isEmpty()) ImageLoader.loadImage(getApplicationContext(), song.getImage(), imageView);
                else imageView.setImageResource(R.drawable.default_image);

                itemView.setOnClickListener(v -> {
                    Toast.makeText(SongListActivity.this,
                            "Avvio riproduzione: " + song.getTitle(),
                            Toast.LENGTH_SHORT).show();

                    try {
                        if (mp.isPlaying()) {
                            mp.stop();
                        }

                        mp.reset();
                        mp.setDataSource(song.getSongPath());
                        mp.setOnPreparedListener(MediaPlayer::start);
                        mp.setOnErrorListener((mp, what, extra) -> {
                            Toast.makeText(SongListActivity.this,
                                    "Errore durante la riproduzione", Toast.LENGTH_SHORT).show();
                            return true;
                        });
                        mp.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SongListActivity.this,
                                "Errore caricamento audio", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        }
    }
}