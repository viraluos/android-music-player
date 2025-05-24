package com.example.musicplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import com.example.musicplayer.data.api.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private final IBinder binder = new MusicBinder();
    private int currentPosition = 0;
    private List<Song> songList = new ArrayList<>();
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "music_channel";
    private boolean isPrepared = false;

    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music playback controls");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }


    private int lastKnownPosition = 0;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void clearSongList(){ songList.clear(); }
    public void setSongList(List<Song> res){ songList.addAll(res); }

    public void playSong() {
        try {
            currentSong = Song.getCurrentSong();
            currentPosition = songList.indexOf(currentSong);
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            if(currentSong == null) return;

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(currentSong.getSongPath());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playSong(int position) {
        if (mediaPlayer != null) mediaPlayer.release();

        isPrepared = false;

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(songList.get(position).getSongPath());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            togglePlayPause();
            currentPosition = position;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) pause();
            else resume();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            // updateNotification(false, currentSong);
        }
    }

    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            // updateNotification(true, currentSong);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void updateNotification(boolean isPlaying, Song song) {
        PendingIntent playPauseIntent = PendingIntent.getService(
                this,
                0,
                new Intent(this, MusicService.class).setAction("ACTION_TOGGLE_PLAYBACK"),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Azione Play/Pausa
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action.Builder(
                isPlaying ? R.drawable.pause : R.drawable.play,
                isPlaying ? "Pausa" : "Play",
                playPauseIntent
        ).build();

        // Costruzione notifica base
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.default_image)
                .setContentTitle(song.getTitle())
                .setContentText(song.getAuthor())
                .addAction(playPauseAction)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true);

        // Notifica visibile in foreground
        startForeground(NOTIFICATION_ID, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "ACTION_TOGGLE_PLAYBACK".equals(intent.getAction())) {
            togglePlayPause();
        }
        return START_STICKY;
    }

    // Aggiungi listener per preparazione media
    public interface OnMediaPreparedListener {
        void onMediaPrepared();
    }
    private OnMediaPreparedListener mediaPreparedListener;

    public void setOnMediaPreparedListener(OnMediaPreparedListener listener) {
        this.mediaPreparedListener = listener;
    }

    private void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> {
            isPrepared = true;
            if (mediaPreparedListener != null) {
                mediaPreparedListener.onMediaPrepared();
            }
        });
    }

    // Modifica getDuration() per controllare lo stato
    public int getDuration() {
        return (mediaPlayer != null && isPrepared) ? mediaPlayer.getDuration() : 0;
    }

    public boolean getIsPrepared(){ return isPrepared; }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
            lastKnownPosition = position;
        }
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : lastKnownPosition;
    }

    public void playNext() {
        if(songList == null || songList.isEmpty()) return;

        currentPosition = (currentPosition + 1) % songList.size();
        playSong(currentPosition);
    }

    public void playPrevious() {
        if(songList == null || songList.isEmpty()) return;

        currentPosition--;
        if(currentPosition < 0) currentPosition = songList.size() - 1;
        playSong(currentPosition);
    }


}