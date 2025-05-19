package com.example.musicplayer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.data.api.ApiClient;
import com.example.musicplayer.data.api.AuthApiService;
import com.example.musicplayer.data.api.AuthRegisterRequest;
import com.example.musicplayer.data.api.AuthResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, emailInput, passwordInput;
    private MaterialButton registerButton;
    private TextView alreadyHaveAccount;

    private AuthApiService apiService;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.login_button);
        alreadyHaveAccount = findViewById(R.id.register_text);

        apiService = ApiClient.getClient(getApplicationContext()).create(AuthApiService.class);
        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        registerButton.setOnClickListener(v -> attemptRegister());

        alreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });
    }

    private void attemptRegister() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi 👀", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRegisterRequest request = new AuthRegisterRequest(username, email, password);

        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    preferences.edit().putString("token", token).apply();

                    Toast.makeText(RegisterActivity.this, "Registrazione completata 🎉", Toast.LENGTH_SHORT).show();

                    // Vai alla Home o Login
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class); // o LoginActivity
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registrazione fallita: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Errore di rete 😢: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}