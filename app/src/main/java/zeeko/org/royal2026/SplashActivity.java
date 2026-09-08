package zeeko.org.royal2026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // تهيئة الزجاج
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        setupBlurView(findViewById(R.id.blurSplash), rootView, windowBackground);

        // الانتظار 3 ثواني
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("RoyalAppPrefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("isFirstTime", true);

            if (isFirstTime) {
                startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, NetworkCheckActivity.class));
            }
            finish();
        }, 3000);
    }

    private void setupBlurView(BlurView blurView, ViewGroup rootView, Drawable windowBackground) {
        if (blurView != null) {
            blurView.setupWith(rootView, new RenderScriptBlur(this))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(15f);
        }
    }
}
