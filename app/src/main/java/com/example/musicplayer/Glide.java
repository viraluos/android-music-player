package com.example.musicplayer;

import android.content.Context;

import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import okhttp3.OkHttpClient;

@GlideModule
public class Glide extends AppGlideModule {
    @Override
    public void registerComponents(Context context, com.bumptech.glide.Glide glide, Registry registry) {
        OkHttpClient secureClient = ImageLoader.createSecureClient(context);

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory((okhttp3.Call.Factory) secureClient);
        registry.replace(GlideUrl.class, InputStream.class, factory);
    }
}