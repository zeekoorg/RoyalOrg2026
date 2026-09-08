package zeeko.org.royal2026;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class UnityAdsManager {

    private static UnityAdsManager instance;

    private final String GAME_ID = "6186592"; 
    private final String REWARDED_PLACEMENT = "Rewarded_Android";
    private final String BANNER_PLACEMENT = "Banner_Android";
    private final boolean TEST_MODE = false; 

    private boolean isInitialized = false;
    private boolean isAdLoaded = false;
    private boolean isLoadingAd = false;

    // تم التغيير لاستخدام Dialog الزجاجي
    private Dialog loadingDialog;
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

    public void initialize(Context context) {
        if (isInitialized) return;

        UnityAds.initialize(context, GAME_ID, TEST_MODE, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                isInitialized = true;
                loadRewardedAd();
            }
            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                isInitialized = false;
            }
        });
    }

    private void loadRewardedAd() {
        if (isLoadingAd || isAdLoaded) return; 
        isLoadingAd = true;

        UnityAds.load(REWARDED_PLACEMENT, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                isAdLoaded = true;
                isLoadingAd = false;

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

                if (loadingDialog != null && loadingDialog.isShowing()) {
                    cancelTimeout();
                    loadingDialog.dismiss();
                    pendingCallback.onProceed(pendingUrl);
                    clearPendingData();
                }
            }
        });
    }

    public void handleAdRequest(Activity activity, String targetUrl, AdActionCallback callback) {
        if (isAdLoaded) {
            displayAd(activity, targetUrl, callback);
            return;
        }

        showLoadingDialog(activity);

        timeoutHandler = new Handler(Looper.getMainLooper());
        timeoutRunnable = () -> {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                isLoadingAd = false; 
                callback.onProceed(targetUrl); 
                clearPendingData();
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 15000); 

        this.pendingActivity = activity;
        this.pendingUrl = targetUrl;
        this.pendingCallback = callback;

        if (!isLoadingAd) {
            loadRewardedAd();
        }
    }

    private void displayAd(Activity activity, String targetUrl, AdActionCallback callback) {
        UnityAds.show(activity, REWARDED_PLACEMENT, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                isAdLoaded = false;
                callback.onProceed(targetUrl); 
            }
            @Override
            public void onUnityAdsShowStart(String placementId) {}
            @Override
            public void onUnityAdsShowClick(String placementId) {}
            @Override
            public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                isAdLoaded = false; 
                callback.onProceed(targetUrl);
            }
        });
    }

    public void loadBanner(Activity activity, FrameLayout bannerContainer) {
        BannerView bannerView = new BannerView(activity, BANNER_PLACEMENT, new UnityBannerSize(320, 50));
        bannerView.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerView) {
                bannerContainer.removeAllViews();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                );
                bannerContainer.addView(bannerView, params);
            }
            @Override public void onBannerClick(BannerView bannerView) {}
            @Override public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo errorInfo) {}
            @Override public void onBannerLeftApplication(BannerView bannerView) {}
            @Override public void onBannerShown(BannerView bannerView) {}
        });
        bannerView.load();
    }

    // ==========================================
    // تم توحيد تصميم "جاري التحميل" للزجاج هنا
    // ==========================================
    private void showLoadingDialog(Activity activity) {
        if (loadingDialog != null && loadingDialog.isShowing()) return;

        loadingDialog = new Dialog(activity);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_custom_glass);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);

        View decorView = activity.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        BlurView blurView = loadingDialog.findViewById(R.id.blurDialogBg);
        
        if (blurView != null) {
            blurView.setupWith(rootView, new RenderScriptBlur(activity))
                    .setFrameClearDrawable(new ColorDrawable(Color.TRANSPARENT))
                    .setBlurRadius(15f);
        }

        TextView tvTitle = loadingDialog.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = loadingDialog.findViewById(R.id.tvDialogMessage);
        LinearLayout dynamicContainer = loadingDialog.findViewById(R.id.dialogDynamicContainer);

        tvTitle.setText(activity.getString(R.string.ad_dialog_title));
        tvMessage.setText(activity.getString(R.string.loading_ad) + "\n(الرجاء الانتظار قليلاً...)");

        dynamicContainer.removeAllViews();
        ProgressBar progressBar = new ProgressBar(activity);
        progressBar.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFD700")));
        dynamicContainer.addView(progressBar);

        loadingDialog.show();
    }

    private void cancelTimeout() {
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
    }

    private void clearPendingData() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        loadingDialog = null;
        timeoutHandler = null;
        timeoutRunnable = null;
        pendingActivity = null;
        pendingUrl = null;
        pendingCallback = null;
    }
}
