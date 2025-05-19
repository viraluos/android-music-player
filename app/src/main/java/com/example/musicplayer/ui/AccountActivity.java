package com.example.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.Auth;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Auth.isUserLoggedIn(this)) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.account_activity);

        setupLogoutButton();
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
}