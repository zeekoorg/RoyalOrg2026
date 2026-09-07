package zeeko.org.royal2026;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrgPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_preview);

        // 1. حماية الشاشة من التداخل مع شريط الإشعارات (كما فعلنا بالشاشة الرئيسية)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.preview_main_container), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // 2. تفعيل زر الرجوع لإغلاق الشاشة والعودة للرئيسية
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // 3. تحميل الإعلان (البانر) في حاوية هذه الشاشة باستخدام الكلاس المركزي
        FrameLayout bannerContainer = findViewById(R.id.banner_container_preview);
        UnityAdsManager.getInstance().loadBanner(this, bannerContainer);
    }
}
