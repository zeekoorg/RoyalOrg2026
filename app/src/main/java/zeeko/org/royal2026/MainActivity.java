package zeeko.org.royal2026;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String currentLang;
    
    // تعريف عناصر الواجهة لتحديث نصوصها لاحقاً
    private TextView tvHeaderTitle, tvCopyright;
    private MaterialButton btnDownload2027, btnDownloadVip, btnPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // جلب اللغة المحفوظة أو تعيين الافتراضية
        SharedPreferences prefs = getSharedPreferences("RoyalAppPrefs", MODE_PRIVATE);
        currentLang = prefs.getString("app_lang", "ar");
        setAppLocale(currentLang);
        
        setContentView(R.layout.activity_main);

        // =========================================
        // 1. حماية الشاشة من التداخل مع شريط النظام
        // =========================================
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content_container), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // إضافة مسافات بادئة (Padding) بناءً على ارتفاع شريط الإشعارات وشريط التنقل السفلي
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // ربط العناصر
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvCopyright = findViewById(R.id.tv_copyright);
        btnDownload2027 = findViewById(R.id.btn_download_2027);
        btnDownloadVip = findViewById(R.id.btn_download_vip);
        btnPreview = findViewById(R.id.btn_preview);
        ImageButton btnMenu = findViewById(R.id.btn_menu);
        ImageButton btnLanguage = findViewById(R.id.btn_language);
        FrameLayout bannerContainer = findViewById(R.id.banner_container);

        // فتح القائمة الجانبية
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // تبديل اللغة ديناميكياً
        btnLanguage.setOnClickListener(v -> toggleLanguage());

        // أزرار التحميل والمعاينة
        btnDownload2027.setOnClickListener(v -> showAdRequirementDialog("https://example.com/download_2027"));
        btnDownloadVip.setOnClickListener(v -> showAdRequirementDialog("https://example.com/download_vip"));
        btnPreview.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OrgPreviewActivity.class)));

        // إدارة نقرات القائمة الجانبية
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_privacy) {
                openWebLink("https://www.zeekoorg.com/p/privacy-policy-almalaki.html");
            } else if (id == R.id.nav_telegram) {
                openWebLink("https://t.me/zeeko2025");
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // =========================================
    // 2. منطق الإعلانات والتحميل (مهيأ للكلاس المنفصل)
    // =========================================
    private void showAdRequirementDialog(String downloadUrl) {
        new MaterialAlertDialogBuilder(this, R.style.Theme_RoyalOrg2026_NoActionBar) // يمكن تخصيص ثيم الدايلوج لاحقاً
                .setTitle(R.string.ad_dialog_title)
                .setMessage(R.string.ad_dialog_message)
                .setPositiveButton(R.string.yes_watch, (dialog, which) -> {
                    // هنا سيتم استدعاء كلاس الإعلانات لاحقاً
                    // مثال مستقبلي: AdManager.showRewardAd(this, new AdListener() { ... });
                    
                    boolean isAdReady = false; // قيمة تجريبية

                    if (isAdReady) {
                        // يتم عرض الإعلان وفقط عند (إغلاقه) يفتح الرابط
                        Toast.makeText(this, "جاري عرض الإعلان...", Toast.LENGTH_SHORT).show();
                        // openWebLink(downloadUrl); // تستدعى بعد OnAdClosed
                    } else {
                        // إظهار دايلوج "جاري تحميل إعلان"
                        showLoadingAdDialog();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showLoadingAdDialog() {
        // دايلوج بسيط يفيد بتحميل الإعلان
        Toast.makeText(this, getString(R.string.loading_ad), Toast.LENGTH_LONG).show();
        // سيتم برمجتها بشكل متقدم في كلاس الإعلانات
    }

    // =========================================
    // 3. تغيير اللغة ديناميكياً بدون إعادة رسم الشاشة
    // =========================================
    private void toggleLanguage() {
        currentLang = currentLang.equals("ar") ? "en" : "ar";
        
        // حفظ اللغة الجديدة
        SharedPreferences.Editor editor = getSharedPreferences("RoyalAppPrefs", MODE_PRIVATE).edit();
        editor.putString("app_lang", currentLang);
        editor.apply();

        setAppLocale(currentLang);
        updateUIStrings(); // تحديث النصوص فوراً
    }

    private void setAppLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        // تحديث إعدادات النظام الداخلية للتطبيق
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void updateUIStrings() {
        // تحديث نصوص الشاشة فوراً من الـ Resources الجديدة
        tvHeaderTitle.setText(R.string.app_name);
        btnDownload2027.setText(R.string.download_2027);
        btnDownloadVip.setText(R.string.download_vip);
        btnPreview.setText(R.string.preview_org);
        tvCopyright.setText(R.string.copyright);

        // تحديث نصوص القائمة الجانبية
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_privacy).setTitle(R.string.privacy_policy);
        menu.findItem(R.id.nav_telegram).setTitle(R.string.contact_telegram);
        
        // عكس اتجاه الشاشة (RTL إلى LTR والعكس) ليناسب اللغة فوراً
        drawerLayout.setLayoutDirection(currentLang.equals("ar") ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR);
    }

    // =========================================
    // أدوات مساعدة
    // =========================================
    private void openWebLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}

