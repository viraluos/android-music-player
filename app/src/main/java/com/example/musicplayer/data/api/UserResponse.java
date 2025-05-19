package com.example.musicplayer.data.api;

public class UserResponse {
    public boolean success;
    public User user;

    public static class User {
        public String username;
        public String email;
    }
}