package zeeko.org.royal2026;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkCheckActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView imgNoInternet;
    private TextView tvStatus;
    private MaterialButton btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_check);

        // ربط العناصر
        progressBar = findViewById(R.id.progressBar);
        imgNoInternet = findViewById(R.id.img_no_internet);
        tvStatus = findViewById(R.id.tv_status);
        btnRetry = findViewById(R.id.btn_retry);

        // زر إعادة المحاولة
        btnRetry.setOnClickListener(v -> {
            showLoadingState();
            checkInternetStrictly();
        });

        // بدء الفحص التلقائي عند فتح الشاشة
        checkInternetStrictly();
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        imgNoInternet.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
        tvStatus.setText("جاري التحقق من الاتصال الآمن...");
        tvStatus.setTextColor(getResources().getColor(R.color.white, null));
    }

    private void showErrorState() {
        progressBar.setVisibility(View.GONE);
        imgNoInternet.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
        tvStatus.setText("لا يوجد اتصال فعلي بالإنترنت.\nالرجاء التأكد من الشبكة والمحاولة مجدداً.");
        tvStatus.setTextColor(getResources().getColor(R.color.royal_accent, null));
    }

    /**
     * الدالة الأقوى للتحقق: 
     * 1. تتحقق من حالة الشبكة في الهاتف.
     * 2. تقوم باتصال حقيقي وهمي (Ping) بخوادم الإنترنت لضمان وجود إنترنت فعلي.
     */
    private void checkInternetStrictly() {
        // الخطوة الأولى: التحقق السريع من إعدادات الهاتف
        if (!isNetworkAvailable()) {
            showErrorState();
            return;
        }

        // الخطوة الثانية: التحقق الفعلي (Ping) في Thread منفصل لكي لا يتجمد التطبيق
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean hasActualInternet = hasRealInternetAccess();

            handler.post(() -> {
                if (hasActualInternet) {
                    // الإنترنت حقيقي وممتاز -> ننتقل للشاشة الرئيسية
                    tvStatus.setText("تم الاتصال بنجاح. جاري الدخول...");
                    
                    // تأخير بسيط جداً (نصف ثانية) لتجربة بصرية مريحة
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        startActivity(new Intent(NetworkCheckActivity.this, MainActivity.class));
                        finish(); // إغلاق هذه الشاشة
                    }, 500);

                } else {
                    // الهاتف متصل بشبكة (واي فاي أو بيانات) ولكن لا يوجد إنترنت حقيقي
                    showErrorState();
                }
            });
        });
    }

    // فحص مدير الاتصالات في الأندرويد (هل يوجد أي نوع من الشبكات)
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                       capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                       capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        }
        return false;
    }

    // اتصال حقيقي بخادم موثوق بمهلة زمنية قصيرة
    private boolean hasRealInternetAccess() {
        try {
            // نجرب الاتصال بخوادم جوجل (الأسرع عالمياً)
            HttpURLConnection urlConnection = (HttpURLConnection) 
                    (new URL("https://clients3.google.com/generate_204").openConnection());
            urlConnection.setRequestProperty("User-Agent", "Android");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(2500); // مهلة 2.5 ثانية للاتصال
            urlConnection.setReadTimeout(2500); // مهلة 2.5 ثانية للقراءة
            urlConnection.connect();

            // الاستجابة 204 تعني نجاح الاتصال ووجود إنترنت حقيقي
            return (urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0);
        } catch (IOException e) {
            return false; // فشل الاتصال الفعلي
        }
    }
}

