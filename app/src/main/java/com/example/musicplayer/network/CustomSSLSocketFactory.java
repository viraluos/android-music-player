package com.example.musicplayer.network;

import android.content.Context;

import com.example.musicplayer.R;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class CustomSSLSocketFactory {
    public static OkHttpClient.Builder getClientBuilder(Context context) {
        try {
            // Carica il keystore BKS
            KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream in = context.getResources().openRawResource(R.raw.viraluos);
            trustStore.load(in, "password".toCharArray());

            // Crea TrustManager che usa il nostro keystore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Crea SSLContext che usa il nostro TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Configura OkHttpClient
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(),
                            (X509TrustManager)tmf.getTrustManagers()[0])
                    .hostnameVerifier((hostname, session) -> true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}