// SongDao.java
package com.example.musicplayer.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.Song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SongDao {
    private SongDbHelper dbHelper;

    public SongDao(Context context) {
        dbHelper = new SongDbHelper(context);
    }

    public long insertSong(Song song) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SongDbHelper.COLUMN_TITLE, song.getTitle());
        values.put(SongDbHelper.COLUMN_AUTHOR, song.getAuthor());
        values.put(SongDbHelper.COLUMN_PATH, song.getSongPath());

        long id = db.insert(SongDbHelper.TABLE_SONGS, null, values);
        db.close();
        return id;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Specifica esplicitamente le colonne da selezionare
        String[] columns = {
                SongDbHelper.COLUMN_ID,
                SongDbHelper.COLUMN_TITLE,
                SongDbHelper.COLUMN_AUTHOR,
                SongDbHelper.COLUMN_PATH
        };

        Cursor cursor = db.query(
                SongDbHelper.TABLE_SONGS,
                columns,  // Usa l'array di colonne invece di null
                null, null, null, null, null
        );

        // Ottieni gli indici delle colonne una volta all'inizio
        int idIndex = cursor.getColumnIndex(SongDbHelper.COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(SongDbHelper.COLUMN_TITLE);
        int authorIndex = cursor.getColumnIndex(SongDbHelper.COLUMN_AUTHOR);
        int pathIndex = cursor.getColumnIndex(SongDbHelper.COLUMN_PATH);

        // Verifica che gli indici siano validi
        if (idIndex == -1 || titleIndex == -1 || pathIndex == -1) {
            throw new IllegalStateException("Una o più colonne non esistono nel risultato");
        }

        if (cursor.moveToFirst()) {
            do {
                Song song = new Song(
                        cursor.getString(titleIndex),
                        cursor.getString(authorIndex),
                        cursor.getString(pathIndex)
                );
                song.setId(cursor.getString(idIndex));
                song.setTitle(cursor.getString(titleIndex));

                // L'autore può essere null
                if (authorIndex != -1) {
                    song.setAuthor(cursor.getString(authorIndex));
                }

                song.setSongPath(cursor.getString(pathIndex));
                songs.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return songs;
    }

    public void importFromJson(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.songs);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Song song = new Song(
                        obj.getString("title"),
                        obj.getString("author"),
                        obj.getString("path")
                );
                insertSong(song);
            }
        } catch (Exception e) {
            Log.e("DEBUG_SONG", "Errore durante l'import", e);
        }
    }
}