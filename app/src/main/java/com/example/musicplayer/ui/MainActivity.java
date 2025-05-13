package com.example.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout playlistContainer = findViewById(R.id.playlist_container);

        playlistContainer.setOnClickListener(v -> {

            Intent SongListActivity = new Intent(this, SongListActivity.class);

            TextView tv = findViewById(R.id.pName);
            String get_text = tv.getText().toString();

            Bundle bundle = new Bundle();
            bundle.putString("playlistNameBundle", get_text);

            SongListActivity.putExtras(bundle);
            startActivity(SongListActivity);

        });
    }
}