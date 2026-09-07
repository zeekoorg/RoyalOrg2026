package zeeko.org.royal2026;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class UnityAdsManager {

    private static UnityAdsManager instance;

    // استبدل هذه المعرفات بمعرفات حسابك في يونيتي
    private final String GAME_ID = "6186592"; 
    private final String REWARDED_PLACEMENT = "Rewarded_Android";
    private final String BANNER_PLACEMENT = "Banner_Android";
    private final boolean TEST_MODE = true; // اجعلها false عند النشر النهائي

    private boolean isInitialized = false;
    private boolean isAdLoaded = false;
    private boolean isLoadingAd = false;

    // متغيرات لإدارة الطلب المعلق (أثناء الانتظار 15 ثانية)
    private AlertDialog loadingDialog;
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;
    private Activity pendingActivity;
    private String pendingUrl;
    private AdActionCallback pendingCallback;

    public interface AdActionCallback {
        void onProceed(String url);
    }

    private UnityAdsManager() {}

    public static UnityAdsManager getInstance() {
        if (instance == null) {
            instance = new UnityAdsManager();
        }
        return instance;
    }

    // 1. التهيئة عند فتح التطبيق
    public void initialize(Context context) {
        if (isInitialized) return;

        UnityAds.initialize(context, GAME_ID, TEST_MODE, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                isInitialized = true;
                // تحميل إعلان فور نجاح التهيئة
                loadRewardedAd();
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                isInitialized = false;
            }
        });
    }

    // 2. دالة تحميل الإعلان
    private void loadRewardedAd() {
        if (isLoadingAd || isAdLoaded) return; // منع تكرار الطلب
        isLoadingAd = true;

        UnityAds.load(REWARDED_PLACEMENT, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                isAdLoaded = true;
                isLoadingAd = false;

                // إذا كان هناك مستخدم ينتظر في الدايلوج، نعرض الإعلان فوراً
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    cancelTimeout();
                    loadingDialog.dismiss();
                    displayAd(pendingActivity, pendingUrl, pendingCallback);
                    clearPendingData();
                }
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                isAdLoaded = false;
                isLoadingAd = false;

                // إذا فشل التحميل والمستخدم ينتظر، نفتح الرابط فوراً لكي لا ينزعج
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    cancelTimeout();
                    loadingDialog.dismiss();
                    pendingCallback.onProceed(pendingUrl);
                    clearPendingData();
                }
            }
        });
    }

    // 3. معالجة نقرة المستخدم (التحميل أو الانتظار 15 ثانية)
    public void handleAdRequest(Activity activity, String targetUrl, AdActionCallback callback) {
        if (isAdLoaded) {
            // الإعلان جاهز، يتم عرضه فوراً
            displayAd(activity, targetUrl, callback);
            return;
        }

        // الإعلان غير جاهز، نعرض دايلوج جاري التحميل
        showLoadingDialog(activity);

        // إعداد مؤقت 15 ثانية
        timeoutHandler = new Handler(Looper.getMainLooper());
        timeoutRunnable = () -> {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                isLoadingAd = false; // إلغاء حالة التحميل الوهمية
                callback.onProceed(targetUrl); // فتح الرابط كبديل لعدم توفر إعلان
                clearPendingData();
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 15000); // 15 ثانية

        // حفظ بيانات الطلب المعلق
        this.pendingActivity = activity;
        this.pendingUrl = targetUrl;
        this.pendingCallback = callback;

        // إذا لم يكن هناك طلب جاري مسبقاً، اطلب إعلاناً الآن
        if (!isLoadingAd) {
            loadRewardedAd();
        }
    }

    // 4. عرض الإعلان وتقديم المكافأة "فقط بعد الإغلاق"
    private void displayAd(Activity activity, String targetUrl, AdActionCallback callback) {
        UnityAds.show(activity, REWARDED_PLACEMENT, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                isAdLoaded = false;
                callback.onProceed(targetUrl); // في حال فشل العرض، نفتح الرابط
            }

            @Override
            public void onUnityAdsShowStart(String placementId) {}

            @Override
            public void onUnityAdsShowClick(String placementId) {}

            @Override
            public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                // المستخدم أغلق الإعلان (سواء أكمله أو تخطاه)
                isAdLoaded = false; 
                
                // تقديم المكافأة (فتح الرابط)
                callback.onProceed(targetUrl);

                // ملاحظة: لم نقم باستدعاء loadRewardedAd() هنا بناءً على طلبك
                // لن يتم تحميل إعلان جديد في الخلفية.
            }
        });
    }

    // 5. تحميل البانر في الحاوية المخصصة
    public void loadBanner(Activity activity, FrameLayout bannerContainer) {
        BannerView bannerView = new BannerView(activity, BANNER_PLACEMENT, new UnityBannerSize(320, 50));
        bannerView.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerView) {
                bannerContainer.removeAllViews();
                bannerContainer.addView(bannerView);
            }
            @Override
            public void onBannerClick(BannerView bannerView) {}
            @Override
            public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo errorInfo) {}
            @Override
            public void onBannerLeftApplication(BannerView bannerView) {}
        });
        bannerView.load();
    }

    // --- أدوات مساعدة ---
    private void showLoadingDialog(Activity activity) {
        loadingDialog = new MaterialAlertDialogBuilder(activity, R.style.Theme_RoyalOrg2026_NoActionBar)
                .setTitle(activity.getString(R.string.ad_dialog_title))
                .setMessage(activity.getString(R.string.loading_ad) + "\n(الرجاء الانتظار قليلاً...)")
                .setCancelable(false) // يمنع المستخدم من إغلاقه يدوياً ليتحكم به المؤقت
                .create();
        loadingDialog.show();
    }

    private void cancelTimeout() {
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
    }

    private void clearPendingData() {
        loadingDialog = null;
        timeoutHandler = null;
        timeoutRunnable = null;
        pendingActivity = null;
        pendingUrl = null;
        pendingCallback = null;
    }
}
