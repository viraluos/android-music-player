package com.example.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.MiniPlayerHelper;
import com.example.musicplayer.R;
import com.example.musicplayer.data.api.Song;

public class MainActivity extends AppCompatActivity {

    private MiniPlayerHelper mph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mph = MiniPlayerHelper.getInstance(this);
        mph.setListener(new MiniPlayerHelper.MusicPlayerListener() {
            @Override
            public void onServiceConnected() {
                updateMiniPlayer();
            }

            @Override
            public void onServiceDisconnected() {
// se necessario
            }
        });

        if (!mph.getIsBound()) updateMiniPlayer();

        mph.bindService(this);

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

    private void updateMiniPlayer() {
        Song currentSong = mph.getCurrentSong();
        if (currentSong != null) {
            TextView title = findViewById(R.id.song_title);
            TextView artist = findViewById(R.id.song_artist);
            ImageView cover = findViewById(R.id.song_cover);
            ImageView play_pause = findViewById(R.id.btn_play_pause);

            mph.updateMiniPlayerUI(currentSong, title, artist, cover, play_pause);

            mph.setupMiniPlayerControls(findViewById(R.id.miniPlayer));
            findViewById(R.id.miniPlayer).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mph.getIsBound()) mph.bindService(this);
        updateMiniPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mph.unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mph.unbindService(this);
    }
}