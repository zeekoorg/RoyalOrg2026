package zeeko.org.royal2026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 ثواني

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // استخدام Handler للانتظار 3 ثواني قبل الانتقال
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            
            // فتح ملف الإعدادات المحفوظة للتحقق
            SharedPreferences prefs = getSharedPreferences("RoyalAppPrefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("isFirstTime", true);

            if (isFirstTime) {
                // إذا كانت أول مرة -> شاشة اللغات
                startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
            } else {
                // إذا لم تكن أول مرة -> شاشة التحقق من الاتصال
                startActivity(new Intent(SplashActivity.this, NetworkCheckActivity.class));
            }
            
            // إغلاق شاشة البداية حتى لا يعود إليها المستخدم عند الضغط على زر الرجوع
            finish();

        }, SPLASH_DELAY);
    }
}
