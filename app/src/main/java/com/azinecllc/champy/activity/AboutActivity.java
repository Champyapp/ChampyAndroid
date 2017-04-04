package com.azinecllc.champy.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.azinecllc.champy.R;

import static com.azinecllc.champy.utils.Constants.azinecUrl;

public class AboutActivity extends AppCompatActivity {

    private WebView webView;
    private View spinner;
    private TextView tvAbout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("About");

        tvAbout = (TextView) findViewById(R.id.text_view_about);

        runOnUiThread(() -> {
            tvAbout.setText(getResources().getString(R.string.about_large_text));
        });


//        spinner = findViewById(R.id.loadingPanel);
//        spinner.setVisibility(View.VISIBLE);

//        runOnUiThread(() -> {
//            webView = (WebView) findViewById(R.id.webView);
//            webView.getSettings().setJavaScriptEnabled(true);
//            webView.setWebViewClient(new WebViewClient() {
//                @Override
//                public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                }
//                @Override
//                public void onPageFinished(WebView view, String url) {
//                    spinner.setVisibility(View.GONE);
//                }
//            });
//            webView.loadUrl(azinecUrl);
//        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


}
