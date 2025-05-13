package com.example.musicplayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Scanner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class ImageLoader {
    private static OkHttpClient secureClient;

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (secureClient == null) {
            secureClient = createSecureClient(context);
        }

        new Thread(() -> {
            // Test diretto della connessione
            testImageConnection(url);
        }).start();

        Glide.with(context)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {

                        new Thread(() -> {
                            // Test diretto della connessione
                            testImageConnection(url);
                        }).start();

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .error(R.drawable.error_image))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    public static OkHttpClient createSecureClient(Context context) {
        try {
            // Carica il certificato personalizzato
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certInput = context.getResources().openRawResource(R.raw.viraluos_cert);
            Certificate cert = cf.generateCertificate(certInput);

            // Crea un KeyStore personalizzato
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("viraluos", cert);

            // Crea un TrustManager che usa il nostro KeyStore
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Crea SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(),
                            (X509TrustManager)tmf.getTrustManagers()[0])
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void testImageConnection(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            //String contentType = connection.getContentType();
            //long contentLength = connection.getContentLengthLong();
            //Log.e("IMAGE_CONNECTION", "Response Code: " + responseCode);
            //Log.e("IMAGE_CONNECTION", "Content Type: " + contentType);
            //Log.e("IMAGE_CONNECTION", "Content Length: " + contentLength);

            if (responseCode >= 400) {
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    String errorResponse = new Scanner(errorStream).useDelimiter("\\A").next();
                    Log.e("IMAGE_CONNECTION", "Error Response: " + errorResponse);
                }
            }

            connection.disconnect();
        } catch (Exception e) {
            Log.e("IMAGE_CONNECTION", "Errore durante il test: " + e.getMessage() + ", per: " + imageUrl);
        }
    }
}
