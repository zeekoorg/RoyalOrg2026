package zeeko.org.royal2026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        // تهيئة الزجاج
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        setupBlurView(findViewById(R.id.blurArabic), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurEnglish), rootView, windowBackground);

        LinearLayout btnArabic = findViewById(R.id.btn_arabic_click);
        LinearLayout btnEnglish = findViewById(R.id.btn_english_click);

        btnArabic.setOnClickListener(v -> saveLanguageAndContinue("ar"));
        btnEnglish.setOnClickListener(v -> saveLanguageAndContinue("en"));
    }

    private void saveLanguageAndContinue(String langCode) {
        SharedPreferences prefs = getSharedPreferences("RoyalAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_lang", langCode);
        editor.putBoolean("isFirstTime", false);
        editor.apply();

        startActivity(new Intent(LanguageActivity.this, NetworkCheckActivity.class));
        finish();
    }

    private void setupBlurView(BlurView blurView, ViewGroup rootView, Drawable windowBackground) {
        if (blurView != null) {
            blurView.setupWith(rootView, new RenderScriptBlur(this))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(15f);
        }
    }
}
