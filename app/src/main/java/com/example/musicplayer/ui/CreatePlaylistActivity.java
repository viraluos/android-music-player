package com.example.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.musicplayer.Auth;
import com.example.musicplayer.R;
import com.example.musicplayer.data.api.ApiClient;
import com.example.musicplayer.data.api.PlaylistApiService;
import com.example.musicplayer.data.api.PlaylistRequest;
import com.example.musicplayer.data.api.PlaylistResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePlaylistActivity extends AppCompatActivity {
    private EditText playlistNameInput;
    private Button createButton;
    private PlaylistApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        apiService = ApiClient.getClient(this).create(PlaylistApiService.class);
        playlistNameInput = findViewById(R.id.playlist_name_input);
        createButton = findViewById(R.id.create_button);

        createButton.setOnClickListener(v -> createPlaylist());
    }

    private void createPlaylist() {
        String name = playlistNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            playlistNameInput.setError("Inserisci un nome");
            return;
        }

        String token = Auth.getToken(this);
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        PlaylistRequest request = new PlaylistRequest(name);
        apiService.createPlaylist(token, request).enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreatePlaylistActivity.this, "Playlist creata!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                Toast.makeText(CreatePlaylistActivity.this, "Errore nella creazione", Toast.LENGTH_SHORT).show();
            }
        });
    }
}