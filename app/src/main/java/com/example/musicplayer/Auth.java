package com.example.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;

public class Auth {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    public static boolean isUserLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AUTH_TOKEN, null) != null;
    }

    public static void setLoggedIn(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.apply();
    }
}