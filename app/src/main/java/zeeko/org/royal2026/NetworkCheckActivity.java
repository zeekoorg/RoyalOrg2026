package zeeko.org.royal2026;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class NetworkCheckActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView imgNoInternet;
    private TextView tvStatus;
    private LinearLayout btnRetryClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_check);

        // تهيئة تأثير الزجاج للكرت
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        setupBlurView(findViewById(R.id.blurNetworkCard), rootView, windowBackground);

        // ربط العناصر
        progressBar = findViewById(R.id.progressBar);
        imgNoInternet = findViewById(R.id.img_no_internet);
        tvStatus = findViewById(R.id.tv_status);
        btnRetryClick = findViewById(R.id.btn_retry_click);

        // زر إعادة المحاولة
        btnRetryClick.setOnClickListener(v -> {
            showLoadingState();
            checkInternetStrictly();
        });

        // بدء الفحص التلقائي
        checkInternetStrictly();
    }

    private void setupBlurView(BlurView blurView, ViewGroup rootView, Drawable windowBackground) {
        if (blurView != null) {
            blurView.setupWith(rootView, new RenderScriptBlur(this))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(15f);
        }
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        imgNoInternet.setVisibility(View.GONE);
        btnRetryClick.setVisibility(View.GONE);
        tvStatus.setText(getString(R.string.checking_internet));
        tvStatus.setTextColor(getResources().getColor(android.R.color.white, null));
    }

    private void showErrorState() {
        progressBar.setVisibility(View.GONE);
        imgNoInternet.setVisibility(View.VISIBLE);
        btnRetryClick.setVisibility(View.VISIBLE);
        tvStatus.setText(getString(R.string.no_internet));
        // استخدام اللون الأحمر للتنبيه
        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
    }

    private void checkInternetStrictly() {
        if (!isNetworkAvailable()) {
            showErrorState();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean hasActualInternet = hasRealInternetAccess();

            handler.post(() -> {
                if (hasActualInternet) {
                    tvStatus.setText(getString(R.string.connected_success));
                    tvStatus.setTextColor(getResources().getColor(android.R.color.white, null));
                    
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        startActivity(new Intent(NetworkCheckActivity.this, MainActivity.class));
                        finish();
                    }, 500);
                } else {
                    showErrorState();
                }
            });
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                       capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                       capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        }
        return false;
    }

    private boolean hasRealInternetAccess() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) 
                    (new URL("https://clients3.google.com/generate_204").openConnection());
            urlConnection.setRequestProperty("User-Agent", "Android");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(2500);
            urlConnection.setReadTimeout(2500);
            urlConnection.connect();

            return (urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0);
        } catch (IOException e) {
            return false;
        }
    }
}
