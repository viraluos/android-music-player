package com.example.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Trova il container della playlist
        LinearLayout playlistContainer = findViewById(R.id.playlist_container);

        // Imposta il click listener per "Tutte le canzoni"
        playlistContainer.setOnClickListener(v -> {
            // Avvia l'activity della lista canzoni
            startActivity(new Intent(this, SongListActivity.class));
        });
    }
}