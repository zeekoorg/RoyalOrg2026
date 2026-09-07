package zeeko.org.royal2026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        MaterialButton btnArabic = findViewById(R.id.btn_arabic);
        MaterialButton btnEnglish = findViewById(R.id.btn_english);

        // عند الضغط على العربية
        btnArabic.setOnClickListener(v -> saveLanguageAndContinue("ar"));

        // عند الضغط على الإنجليزية
        btnEnglish.setOnClickListener(v -> saveLanguageAndContinue("en"));
    }

    private void saveLanguageAndContinue(String langCode) {
        // حفظ اللغة المنتقاة وإلغاء حالة أول مرة دخول
        SharedPreferences prefs = getSharedPreferences("RoyalAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString("app_lang", langCode);
        editor.putBoolean("isFirstTime", false); // لن تظهر هذه الشاشة مرة أخرى
        editor.apply();

        // الانتقال إلى شاشة التحقق من الاتصال
        Intent intent = new Intent(LanguageActivity.this, NetworkCheckActivity.class);
        startActivity(intent);
        
        // إغلاق شاشة اللغات
        finish();
    }
}

