package com.example.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.musicplayer.PlayerHelper;
import com.example.musicplayer.R;
import com.example.musicplayer.Auth;
import com.example.musicplayer.data.api.ApiClient;
import com.example.musicplayer.data.api.AuthApiService;
import com.example.musicplayer.data.api.Playlist;
import com.example.musicplayer.data.api.Song;
import com.example.musicplayer.data.api.SongApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private PlayerHelper mph;
    private SongApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mph = PlayerHelper.getInstance(this);
        mph.setListener(new PlayerHelper.MusicPlayerListener() {
            @Override
            public void onServiceConnected() { updateMiniPlayer(); }

            @Override
            public void onServiceDisconnected() {}
        });

        mph.bindService(this);

        if (!mph.getIsBound()) updateMiniPlayer();

        ImageView profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setOnClickListener(v -> {
            if (Auth.isUserLoggedIn(this)) {
                startActivity(new Intent(this, AccountActivity.class));
            }
            else {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.putExtra("redirect_target", "account");
                startActivity(loginIntent);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        TextView tv = findViewById(R.id.general);

        tv.setOnClickListener(v -> {

            Intent SongListActivity = new Intent(this, SongListActivity.class);

            String get_text = tv.getText().toString();

            Bundle bundle = new Bundle();
            bundle.putString("playlistNameBundle", "general");

            SongListActivity.putExtras(bundle);
            startActivity(SongListActivity);

        });

        apiService = ApiClient.getClient(getApplicationContext()).create(SongApiService.class);

        fetchRandomSongs();
        fetchPlaylistsDynamically();
    }

    private void updateMiniPlayer() {
        Song currentSong = Song.getCurrentSong();
        if (currentSong != null) {
            TextView title = findViewById(R.id.song_title);
            TextView artist = findViewById(R.id.song_artist);
            ImageView cover = findViewById(R.id.song_cover);
            ImageView play_pause = findViewById(R.id.btn_play_pause);

            mph.updateMiniPlayerUI(currentSong, title, artist, cover, play_pause);

            mph.setupMiniPlayerControls(findViewById(R.id.miniPlayer));
            findViewById(R.id.miniPlayer).setVisibility(View.VISIBLE);
        }
        else findViewById(R.id.miniPlayer).setVisibility(View.GONE);
    }

    private void fetchRandomSongs() {
        apiService.getRandomSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful()) {
                    List<Song> songs = response.body();

                    for (int i = 0; i < songs.size(); i++) {
                        Song song = songs.get(i);

                        LinearLayout ll = findViewById(getResources().getIdentifier(
                                "boxRandomSong" + (i + 1), "id", getPackageName()
                        ));
                        TextView titleView = findViewById(getResources().getIdentifier(
                                "idRandomSong" + (i + 1), "id", getPackageName()
                        ));
                        ImageView imageView = findViewById(getResources().getIdentifier(
                                "imageRandomSong" + (i + 1), "id", getPackageName()
                        ));

                        ll.setOnClickListener(v -> {
                            Song.setCurrentSong(song);
                            mph.playSong();
                            updateMiniPlayer();
                        });

                        titleView.setText(song.title);
                        Glide.with(MainActivity.this)
                                .load(song.image)
                                .placeholder(R.drawable.placeholder)  // opzionale
                                .error(R.drawable.error_image)        // opzionale
                                .into(imageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("Retrofit", "Errore: " + t.getMessage());
            }
        });
    }

    private void fetchPlaylistsDynamically() {
        apiService.getPlaylists().enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful()) {
                    List<Playlist> playlists = response.body();
                    LinearLayout playlistContainer = findViewById(R.id.playlist_container);

                    for (Playlist playlist : playlists) {
                        TextView playlistView = new TextView(MainActivity.this);
                        playlistView.setText(playlist.getName());
                        playlistView.setTextSize(18);
                        playlistView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.primary_text));
                        playlistView.setPadding(14, 14, 14, 14);
                        playlistView.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_background));

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(0, 0, 0, 10);
                        playlistView.setLayoutParams(layoutParams);

                        playlistView.setOnClickListener(v -> {
                            Intent intent = new Intent(MainActivity.this, SongListActivity.class);
                            intent.putExtra("playlistNameBundle", playlist.getName());
                            startActivity(intent);
                        });

                        playlistContainer.addView(playlistView);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.e("Retrofit", "Errore: " + t.getMessage());
            }
        });
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