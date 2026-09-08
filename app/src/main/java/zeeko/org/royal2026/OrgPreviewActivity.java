package zeeko.org.royal2026;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class OrgPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_preview);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.preview_main_container), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // تهيئة الزجاج لكل العناصر
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        setupBlurView(findViewById(R.id.blurBackBtn), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurImage1), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurImage2), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurImage3), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurImage4), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurImage5), rootView, windowBackground);

        // زر الرجوع
        LinearLayout btnBack = findViewById(R.id.btn_back_click);
        btnBack.setOnClickListener(v -> finish());

        // البانر
        FrameLayout bannerContainer = findViewById(R.id.banner_container_preview);
        UnityAdsManager.getInstance().loadBanner(this, bannerContainer);
    }

    private void setupBlurView(BlurView blurView, ViewGroup rootView, Drawable windowBackground) {
        if (blurView != null) {
            blurView.setupWith(rootView, new RenderScriptBlur(this))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(15f);
        }
    }
}
