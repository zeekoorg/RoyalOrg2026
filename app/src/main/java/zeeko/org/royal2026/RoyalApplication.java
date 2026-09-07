package zeeko.org.royal2026;

import android.app.Application;

public class RoyalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // تهيئة الإعلانات على مستوى التطبيق بالكامل
        UnityAdsManager.getInstance().initialize(this);
    }
}
