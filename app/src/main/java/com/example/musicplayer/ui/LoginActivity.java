package com.example.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.Auth;
import com.example.musicplayer.databinding.LoginActivityBinding;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupLoginButton();
        setupRegisterText();
    }

    private void setupLoginButton() {
        binding.loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        // Reset errors
        binding.emailLayout.setError(null);
        binding.passwordLayout.setError(null);

        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailLayout.setError(getString(R.string.email_required));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError(getString(R.string.invalid_email));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordLayout.setError(getString(R.string.password_required));
            return;
        }

        if (password.length() < 6) {
            binding.passwordLayout.setError(getString(R.string.password_too_short));
            return;
        }

        // Simulazione login (sostituisci con la tua logica reale)
        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        binding.loginButton.setEnabled(false);
        binding.loginButton.setText(R.string.loading);

        AuthApiService authApi = ApiClient.getClient(this).create(AuthApiService.class);
        Call<LoginResponse> call = authApi.login(email, password);

        call.enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                binding.loginButton.setEnabled(true);
                binding.loginButton.setText(R.string.login);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.success) {
                        Auth.setLoggedIn(LoginActivity.this, loginResponse.token);
                        onLoginSuccess();
                    } else {
                        onLoginFailed(loginResponse.message != null ? loginResponse.message : getString(R.string.login_error));
                    }
                } else {
                    onLoginFailed(getString(R.string.login_error));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                binding.loginButton.setEnabled(true);
                binding.loginButton.setText(R.string.login);
                onLoginFailed("Errore di rete: " + t.getLocalizedMessage());
            }
        });
    }

    private boolean isValidCredentials(String email, String password) {
        // Mock: accetta qualsiasi password > 5 caratteri per email valida
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() > 5;
    }

    private void onLoginSuccess() {
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();

        // Gestione redirect dopo login
        if (getIntent().hasExtra("redirect_target")) {
            String target = getIntent().getStringExtra("redirect_target");
            if ("account".equals(target)) {
                startActivity(new Intent(this, AccountActivity.class));
            }
        }
        finish(); // Chiudi LoginActivity
    }

    private void onLoginFailed(String error) {
        binding.passwordLayout.setError(error);
        binding.passwordInput.requestFocus();
        binding.loginButton.setEnabled(true);
        binding.loginButton.setText(R.string.login);
    }

    private void setupRegisterText() {
        binding.registerText.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}