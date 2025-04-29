package com.example.musicplayer;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.data.local.SongDao;
import com.example.musicplayer.data.local.SongDbHelper;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verifica la struttura del database
        SongDbHelper dbHelper = new SongDbHelper(this);
        dbHelper.isTableValid();
        dbHelper.close();

        SongDao songDao = new SongDao(this);

        if(songDao.getAllSongs().isEmpty()) {
            songDao.importFromJson(getApplicationContext());
        }

        Log.v("DEBUG_SONG", songDao.toString());

    }
}
