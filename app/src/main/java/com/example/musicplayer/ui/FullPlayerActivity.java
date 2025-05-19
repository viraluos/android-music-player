package com.example.musicplayer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.ImageLoader;
import com.example.musicplayer.MusicService;
import com.example.musicplayer.PlayerHelper;
import com.example.musicplayer.R;
import com.example.musicplayer.data.api.Song;

public class FullPlayerActivity extends AppCompatActivity {
    private PlayerHelper mph;
    private SeekBar seekBar;
    private TextView currentTime, totalDuration, songTitle, artist;
    private ImageView coverArt, playPauseBtn, prevBtn, nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_full);

        mph = PlayerHelper.getInstance(this);
        initializeUI();
        setupControls();
        bindService();
    }

    private void initializeUI() {
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.tempoCanzone);
        totalDuration = findViewById(R.id.DurataCanzone);
        songTitle = findViewById(R.id.nomeCanzone);
        artist = findViewById(R.id.autore);
        coverArt = findViewById(R.id.songPic);
        playPauseBtn = findViewById(R.id.pause);
        prevBtn = findViewById(R.id.bw);
        nextBtn = findViewById(R.id.fw);

        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void bindService() {
        mph.setListener(new PlayerHelper.MusicPlayerListener() {
            @Override
            public void onServiceConnected() {
                updatePlayerUI(); // Forza l'aggiornamento iniziale
                if(mph.isPlaying()) {
                    startUpdatingProgress();
                } else {
                    // Aggiorna manualmente lo stato se in pausa
                    new Handler().post(() -> {
                        updatePlayerUI();
                    });
                }
            }

            @Override
            public void onServiceDisconnected() {}
        });
        mph.bindService(this);
    }

    private void updatePlayerUI() {
        if (mph.getMusicService() != null) {
            MusicService service = mph.getMusicService();
            Song currentSong = Song.getCurrentSong();

            if (currentSong != null) {
                songTitle.setText(currentSong.getTitle());
                artist.setText(currentSong.getAuthor());

                ImageLoader.loadImage(this, currentSong.getImage(), coverArt);
            }

            seekBar.setMax(service.getDuration());
            seekBar.setProgress(service.getCurrentPosition());
            totalDuration.setText(service.getDuration());
            currentTime.setText(formatTime(service.getCurrentPosition()));

            mph.updatePlayPauseIcon(playPauseBtn);
        }
    }

    private int formatStringDuration(String duration){
        int formattedDuration = 0;

        try {
            String[] parts = duration.split(":");

            if (parts.length == 2) {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);

                formattedDuration = (minutes * 60 + seconds) * 1000;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return formattedDuration;
    }

    private void setupControls() {
        // SeekBar controls
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    currentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mph.stopUpdatingTime();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mph.getMusicService() != null) {
                    mph.seekTo(seekBar.getProgress());
                    currentTime.setText(formatTime(seekBar.getProgress()));

                    if(mph.isPlaying()) {
                        mph.startUpdatingTime(currentTime, seekBar);
                    }
                }
            }
        });

        // Play/Pause button
        playPauseBtn.setOnClickListener(v -> {
            mph.getMusicService().togglePlayPause();
            mph.updatePlayPauseIcon(playPauseBtn);
            if(mph.isPlaying()) startUpdatingProgress();
        });

        prevBtn.setOnClickListener(v -> {
            mph.playPrevious();
            new Handler(Looper.getMainLooper()).postDelayed(this::updatePlayerUI, 100);
        });

        nextBtn.setOnClickListener(v -> {
            mph.playNext();
            new Handler(Looper.getMainLooper()).postDelayed(this::updatePlayerUI, 100);
        });

        updatePlayerState();
    }

    private void updatePlayerState() {
        if(mph.getMusicService() != null) {
            seekBar.setMax(mph.getDuration());
            seekBar.setProgress(mph.getCurrentPosition());
            totalDuration.setText(formatTime(mph.getDuration()));
            currentTime.setText(formatTime(mph.getCurrentPosition()));
        }
    }

    private void startUpdatingProgress() {
        mph.startUpdatingTime(currentTime, seekBar);
        new Handler(Looper.getMainLooper()).post(() -> {
            if(mph.isPlaying()) {
                seekBar.setMax(mph.getDuration());
                totalDuration.setText(formatTime(mph.getDuration()));
            }
        });
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerState();
        if(mph.isPlaying()) {
            startUpdatingProgress();
            updatePlayerUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mph.unbindService(this);
    }
}