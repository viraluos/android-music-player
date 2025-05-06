package com.example.musicplayer.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.musicplayer.ImageLoader;
import com.example.musicplayer.R;
import com.example.musicplayer.data.api.ApiClient;
import com.example.musicplayer.data.api.SongApiService;
import com.example.musicplayer.data.api.Song;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
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

        SongApiService apiService = ApiClient.getClient(getApplicationContext()).create(SongApiService.class);
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
        ImageView imageView = view.findViewById(R.id.songImage);

        titleView.setText(song.getTitle());
        authorView.setText(song.getAuthor());

        if (song.getImage() != null && !song.getImage().isEmpty()) {
            String imageUrl = song.getImage();

            ImageLoader.loadImage(getApplicationContext(), imageUrl, imageView);
        }
        else imageView.setImageResource(R.drawable.default_image); // default

        view.setOnClickListener(v -> {
            Toast.makeText(this, "Avvio riproduzione: " + song.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: logica di riproduzione qui | playSong(song.getSongPath());
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