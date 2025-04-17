package com.example.musicplayer;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SongListActivity extends AppCompatActivity {

    private LinearLayout songsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songsContainer = findViewById(R.id.songs_container);

        // ...
    }
}
