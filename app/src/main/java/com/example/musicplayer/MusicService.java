package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SeekBar;
import android.widget.TextView;

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
            currentPosition = songList.indexOf(currentSong); // AGGIUNGI QUESTA RIGA
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

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(songList.get(position).getSongPath());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            togglePlayPause();
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
            updateNotification(false);
        }
    }

    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateNotification(true);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void updateNotification(boolean isPlaying) {
        // Implementa l'aggiornamento della notifica
        // con l'icona corretta (play/pause)
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
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