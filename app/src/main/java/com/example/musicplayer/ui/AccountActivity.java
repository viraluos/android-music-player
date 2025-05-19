package com.example.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.Auth;
import com.example.musicplayer.data.api.ApiClient;
import com.example.musicplayer.data.api.AuthApiService;
import com.example.musicplayer.data.api.AuthResponse;
import com.example.musicplayer.data.api.UserResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Auth.isUserLoggedIn(this)) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_account);
        loadUserData();
        setupLogoutButton();
        fetchUserData();
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class)
                .putExtra("redirect_target", "account"));
        finish();
    }

    private void setupLogoutButton() {
        findViewById(R.id.logout_button).setOnClickListener(v -> {
            Auth.logout(this);
            Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();

            redirectToLogin();
            finish();
        });
    }

    private void loadUserData() {
        String token = Auth.getToken(this);
        if (token == null) return;

        AuthApiService api = ApiClient.getClient(this).create(AuthApiService.class);
        Call<UserResponse> call = api.getUser("Bearer " + token);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    UserResponse.User user = response.body().user;

                    TextView usernameText = findViewById(R.id.username_text);
                    TextView emailText = findViewById(R.id.email_text);

                    usernameText.setText(user.username);
                    emailText.setText(user.email);
                } else {
                    Toast.makeText(AccountActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AccountActivity.this, "Errore di rete: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserData() {
        String token = Auth.getToken(this);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token mancante", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("https://viraluos.com/musxfy/api/android_app/user.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    JSONObject json = new JSONObject(response.toString());
                    if (json.getBoolean("success")) {
                        JSONObject user = json.getJSONObject("user");
                        String username = user.getString("username");
                        String email = user.getString("email");

                        runOnUiThread(() -> updateUI(username, email));
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Utente non trovato", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Errore: " + responseCode, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Errore rete: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void updateUI(String username, String email) {
        TextView usernameText = findViewById(R.id.username_text);
        TextView emailText = findViewById(R.id.email_text);

        usernameText.setText(username);
        emailText.setText(email);
    }


}