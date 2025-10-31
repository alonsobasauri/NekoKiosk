package com.neko.kiosk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        FrameLayout layout = new FrameLayout(this);

        WebView webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(1200, 1440);
        webView.setLayoutParams(params);

        webView.loadUrl("https://mqok.portal.smartsystems.work/?cast=1&usr=user&pwd=br4zOPw8fdZhtOhw");

        layout.addView(webView);
        setContentView(layout);

        // Check for updates
        UpdateChecker updateChecker = new UpdateChecker(this);
        updateChecker.checkForUpdates();
    }
}
