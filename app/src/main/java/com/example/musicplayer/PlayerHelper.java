package com.example.musicplayer;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.data.api.Song;
import com.example.musicplayer.ui.FullPlayerActivity;

import java.util.List;

public class PlayerHelper<View> {
    private static PlayerHelper instance;
    private MusicService musicService;
    private boolean isBound = false;
    private Context context;
    private ServiceConnection connection;
    private MusicPlayerListener listener;

    private Handler timeHandler = new Handler();
    private Runnable timeUpdater;
    private boolean isUpdatingTime = false;

    public boolean getIsBound(){ return isBound; }

    public boolean isPlaying() { return musicService.isPlaying(); }

    public interface MusicPlayerListener {
        void onServiceConnected();
        void onServiceDisconnected();
    }

    private PlayerHelper(Context context) {
        this.context = context.getApplicationContext();
        initializeServiceConnection();
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public static synchronized PlayerHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerHelper(context);
        }
        return instance;
    }

    private void initializeServiceConnection() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                musicService = binder.getService();
                isBound = true;
                if (listener != null) {
                    listener.onServiceConnected();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                isBound = false;
                if (listener != null) {
                    listener.onServiceDisconnected();
                }
            }
        };
    }

    public void bindService(Context activityContext) {
        Intent serviceIntent = new Intent(activityContext, MusicService.class);
        activityContext.startService(serviceIntent);
        activityContext.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(Context activityContext) {
        if (isBound) {
            activityContext.unbindService(connection);
            isBound = false;
        }
    }

    public void playSong() {
        musicService.playSong();
    }

    public void playSong(int currentPosition){ musicService.playSong(currentPosition); }

    public void seekTo(int position) {
        if (isBound && musicService != null) {
            musicService.seekTo(position);
        }
    }

    public int getDuration() {
        return (musicService != null) ? musicService.getDuration() : 0;
    }

    public int getCurrentPosition() {
        Log.e("IsPreparedPlayerHelper", String.valueOf(musicService.getIsPrepared()));
        return (musicService != null && musicService.getIsPrepared()) ? musicService.getCurrentPosition() : 0;
    }
    public void setCurrentPosition(int position){ musicService.setCurrentPosition(position); }

    public void playNext() {
        Log.e("bound", Boolean.toString(isBound));
        musicService.playNext();
    }

    public void playPrevious() {
        musicService.playPrevious();
    }

    private void updateTimeText(int milliseconds, TextView textView, SeekBar seek) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        textView.setText(String.format("%02d:%02d", minutes, seconds));

        seek.setProgress(milliseconds);
    }
    public void startUpdatingTime(TextView timeTextView, SeekBar seekBar) {
        if (isUpdatingTime) {
            timeHandler.removeCallbacks(timeUpdater);
            isUpdatingTime = false;
        }
        isUpdatingTime = true;

        timeUpdater = new Runnable() {
            @Override
            public void run() {
                if (musicService != null && musicService.isPlaying()) {
                    int currentPosition = musicService.getCurrentPosition();
                    updateTimeText(currentPosition, timeTextView, seekBar);
                    timeHandler.postDelayed(this, 500);
                }
            }
        };

        timeHandler.post(timeUpdater);
    }
    public void stopUpdatingTime() {
        if (isUpdatingTime) {
            timeHandler.removeCallbacks(timeUpdater);
            isUpdatingTime = false;
        }
    }

    public void clearSongList(){ musicService.clearSongList(); }
    public void setSongList(List<Song> res){ musicService.setSongList(res); }

    public Song getCurrentSong() {
        return isBound ? Song.getCurrentSong() : null;
    }

    public void setListener(MusicPlayerListener listener) {
        this.listener = listener;
    }

    public void updateMiniPlayerUI(Song pass_song, TextView titleView, TextView artistView, ImageView coverView, ImageView play_pause) {

        Song song = Song.getCurrentSong() != null ? Song.getCurrentSong() : pass_song;

        titleView.setText(song.getTitle());
        artistView.setText(song.getAuthor());
        String imageUrl = song.getImage();

        timeUpdater = new Runnable() {
            @Override
            public void run() {
                if (musicService != null && musicService.isPlaying()) {
                    updatePlayPauseIcon(play_pause);
                    timeHandler.postDelayed(this, 2500);
                }
            }
        };

        if (imageUrl != null && !imageUrl.isEmpty()) ImageLoader.loadImage(context, imageUrl, coverView);
        else coverView.setImageResource(R.drawable.default_image);
    }

    public void setOnMediaPreparedListener(MusicService.OnMediaPreparedListener listener) {
        if (musicService != null) {
            musicService.setOnMediaPreparedListener(listener);
        }
    }

    public void setupMiniPlayerControls(android.view.View miniPlayerView) {
        ImageView playPauseButton = miniPlayerView.findViewById(R.id.btn_play_pause);
        playPauseButton.setOnClickListener(v -> {
            musicService.togglePlayPause();
            updatePlayPauseIcon(playPauseButton);
        });

        miniPlayerView.setOnClickListener(v -> {
            Intent intent = new Intent(miniPlayerView.getContext(), FullPlayerActivity.class);
            startActivity(miniPlayerView.getContext(), intent, null);
        });
    }

    public void updatePlayPauseIcon(ImageView playPauseButton) {
        playPauseButton.setImageResource( musicService.isPlaying() ? R.drawable.pause : R.drawable.play );
    }
}