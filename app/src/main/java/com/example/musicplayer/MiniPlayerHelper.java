package com.example.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.example.musicplayer.MusicService;
import com.example.musicplayer.data.api.Song;

public class MiniPlayerHelper<View> {
    private static MiniPlayerHelper instance;
    private MusicService musicService;
    private boolean isBound = false;
    private Context context;
    private ServiceConnection connection;
    private MusicPlayerListener listener;

    public boolean getIsBound(){ return isBound; }

    public interface MusicPlayerListener {
        void onServiceConnected();
        void onServiceDisconnected();
    }

    private MiniPlayerHelper(Context context) {
        this.context = context.getApplicationContext();
        initializeServiceConnection();
    }

    public static synchronized MiniPlayerHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MiniPlayerHelper(context);
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

    public Song getCurrentSong() {
        return isBound ? Song.getCurrentSong() : null;
    }

    public void setListener(MusicPlayerListener listener) {
        this.listener = listener;
    }

    public void updateMiniPlayerUI(Song song, TextView titleView, TextView artistView, ImageView coverView, ImageView play_pause) {
        titleView.setText(song.getTitle());
        artistView.setText(song.getAuthor());
        String imageUrl = song.getImage();

        if (imageUrl != null && !imageUrl.isEmpty()) ImageLoader.loadImage(context, imageUrl, coverView);
        else coverView.setImageResource(R.drawable.default_image);

        updatePlayPauseIcon(play_pause);
    }

    public void setupMiniPlayerControls(android.view.View miniPlayerView) {
        ImageView playPauseButton = miniPlayerView.findViewById(R.id.btn_play_pause);
        playPauseButton.setOnClickListener(v -> {
            musicService.togglePlayPause();
            updatePlayPauseIcon(playPauseButton);
        });
    }

    private void updatePlayPauseIcon(ImageView playPauseButton) {
        playPauseButton.setImageResource( musicService.isPlaying() ? R.drawable.pause : R.drawable.play );
    }
}