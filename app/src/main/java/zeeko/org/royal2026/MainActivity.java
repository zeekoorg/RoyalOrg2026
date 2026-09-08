package zeeko.org.royal2026;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String currentLang;
    
    private TextView tvHeaderTitle, tvCopyright, tvMenuTitle;
    private TextView tvBtnDownload2027, tvBtnDownloadVip, tvBtnPreview;
    private TextView tvNavPrivacy, tvNavTelegram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("RoyalAppPrefs", MODE_PRIVATE);
        currentLang = prefs.getString("app_lang", "ar");
        setAppLocale(currentLang);
        
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content_container), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvCopyright = findViewById(R.id.tv_copyright);
        tvMenuTitle = findViewById(R.id.tv_menu_title);
        
        tvBtnDownload2027 = findViewById(R.id.tv_btn_download_2027);
        tvBtnDownloadVip = findViewById(R.id.tv_btn_download_vip);
        tvBtnPreview = findViewById(R.id.tv_btn_preview);
        
        tvNavPrivacy = findViewById(R.id.tv_nav_privacy);
        tvNavTelegram = findViewById(R.id.tv_nav_telegram);

        LinearLayout btnMenu = findViewById(R.id.btn_menu_click);
        LinearLayout btnLanguage = findViewById(R.id.btn_language_click);
        LinearLayout btnDownload2027 = findViewById(R.id.btn_download_2027_click);
        LinearLayout btnDownloadVip = findViewById(R.id.btn_download_vip_click);
        LinearLayout btnPreview = findViewById(R.id.btn_preview_click);
        
        LinearLayout navPrivacyClick = findViewById(R.id.nav_privacy_click);
        LinearLayout navTelegramClick = findViewById(R.id.nav_telegram_click);
        
        FrameLayout bannerContainer = findViewById(R.id.banner_container);

        // تهيئة تأثير الزجاج (BlurView) لكل الأزرار والقائمة الجانبية
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        setupBlurView(findViewById(R.id.blurMenuBtn), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurLangBtn), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurBtnDownload2027), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurBtnDownloadVip), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurBtnPreview), rootView, windowBackground);
        setupBlurView(findViewById(R.id.blurDrawerContainer), rootView, windowBackground);

        // تحميل البانر الإعلاني
        UnityAdsManager.getInstance().loadBanner(this, bannerContainer);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        btnLanguage.setOnClickListener(v -> toggleLanguage());

        btnDownload2027.setOnClickListener(v -> showGlassAdDialog("https://www.mediafire.com/file/mkz1venvrxb77xa/اورج+2026+الملكي+مهكر.apk/file"));
        btnDownloadVip.setOnClickListener(v -> showGlassAdDialog("https://www.mediafire.com/file/m5t8na96dpymjpg/ORG+ZEEKO+VIP+27.apk/file"));
        btnPreview.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OrgPreviewActivity.class)));

        // نقرات القائمة الجانبية
        navPrivacyClick.setOnClickListener(v -> {
            openWebLink("https://www.zeekoorg.com/p/privacy-policy-almalaki.html");
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        navTelegramClick.setOnClickListener(v -> {
            openWebLink("https://t.me/zeeko2025");
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    private void setupBlurView(BlurView blurView, ViewGroup rootView, Drawable windowBackground) {
        if (blurView != null) {
            blurView.setupWith(rootView, new RenderScriptBlur(this))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(15f); 
        }
    }

    private void showGlassAdDialog(String downloadUrl) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_glass);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        setupBlurView(dialog.findViewById(R.id.blurDialogBg), rootView, new ColorDrawable(Color.TRANSPARENT));

        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvDialogMessage);
        LinearLayout dynamicContainer = dialog.findViewById(R.id.dialogDynamicContainer);

        tvTitle.setText(R.string.ad_dialog_title);
        tvMessage.setText(R.string.ad_dialog_message);

        TextView btnCancel = new TextView(this);
        btnCancel.setText(R.string.cancel);
        btnCancel.setTextColor(Color.WHITE);
        btnCancel.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        btnCancel.setLayoutParams(cancelParams);
        btnCancel.setGravity(android.view.Gravity.CENTER);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        TextView btnWatch = new TextView(this);
        btnWatch.setText(R.string.yes_watch);
        btnWatch.setTextColor(Color.parseColor("#FFD700"));
        btnWatch.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams watchParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        btnWatch.setLayoutParams(watchParams);
        btnWatch.setGravity(android.view.Gravity.CENTER);
        btnWatch.setOnClickListener(v -> {
            dialog.dismiss();
            UnityAdsManager.getInstance().handleAdRequest(MainActivity.this, downloadUrl, url -> openWebLink(url));
        });

        dynamicContainer.addView(btnCancel);
        dynamicContainer.addView(btnWatch);

        dialog.show();
    }

    private void toggleLanguage() {
        currentLang = currentLang.equals("ar") ? "en" : "ar";
        
        SharedPreferences.Editor editor = getSharedPreferences("RoyalAppPrefs", MODE_PRIVATE).edit();
        editor.putString("app_lang", currentLang);
        editor.apply();

        setAppLocale(currentLang);
        updateUIStrings(); 
    }

    private void setAppLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void updateUIStrings() {
        tvHeaderTitle.setText(R.string.app_name);
        tvBtnDownload2027.setText(R.string.download_2027);
        tvBtnDownloadVip.setText(R.string.download_vip);
        tvBtnPreview.setText(R.string.preview_org);
        tvCopyright.setText(R.string.copyright);
        tvMenuTitle.setText(R.string.more_menu);
        tvNavPrivacy.setText(R.string.privacy_policy);
        tvNavTelegram.setText(R.string.contact_telegram);
        
        drawerLayout.setLayoutDirection(currentLang.equals("ar") ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR);
    }

    private void openWebLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
