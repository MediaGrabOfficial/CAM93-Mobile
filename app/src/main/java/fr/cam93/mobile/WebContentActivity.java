package fr.cam93.mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebContentActivity extends Activity {
    private static final int NAVY = Color.rgb(6, 43, 89);
    private static final int YELLOW = Color.rgb(255, 213, 30);
    private static final int WHITE = Color.WHITE;
    private static final int MUTED = Color.rgb(98, 108, 122);

    private WebView webView;
    private ProgressBar progress;
    private TextView toolbarTitle;
    private TextView errorPanel;
    private String initialUrl = "https://cam93.org/";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        String titleValue = getIntent().getStringExtra("title");
        if (titleValue == null || titleValue.trim().isEmpty()) titleValue = "CA Montreuil 93";
        String urlValue = getIntent().getStringExtra("url");
        if (isWebUrl(urlValue)) initialUrl = urlValue;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets safe = insets.getInsets(WindowInsets.Type.systemBars());
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
                return insets;
            });
        }

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(8), dp(6), dp(12), dp(6));
        toolbar.setBackgroundColor(YELLOW);

        TextView back = new TextView(this);
        back.setText("‹");
        back.setTextSize(33);
        back.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        back.setTextColor(NAVY);
        back.setGravity(Gravity.CENTER);
        toolbar.addView(back, new LinearLayout.LayoutParams(dp(42), dp(46)));
        back.setOnClickListener(v -> {
            if (webView != null && webView.canGoBack()) webView.goBack();
            else finish();
        });

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setPadding(dp(5), 0, dp(4), 0);
        toolbarTitle = new TextView(this);
        toolbarTitle.setText(titleValue);
        toolbarTitle.setTextSize(15);
        toolbarTitle.setTextColor(NAVY);
        toolbarTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        toolbarTitle.setMaxLines(1);
        toolbarTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        titleBox.addView(toolbarTitle);
        TextView subtitle = new TextView(this);
        subtitle.setText("Site officiel  ·  cam93.org");
        subtitle.setTextSize(10);
        subtitle.setTextColor(NAVY);
        titleBox.addView(subtitle);
        toolbar.addView(titleBox, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView browser = new TextView(this);
        browser.setText("↗");
        browser.setTextSize(22);
        browser.setTextColor(NAVY);
        browser.setGravity(Gravity.CENTER);
        toolbar.addView(browser, new LinearLayout.LayoutParams(dp(36), dp(42)));
        browser.setOnClickListener(v -> openInBrowser());
        root.addView(toolbar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(60)));

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(NAVY));
        root.addView(progress, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(3)));

        FrameLayout content = new FrameLayout(this);
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.setBackgroundColor(WHITE);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int value) {
                progress.setProgress(value);
                progress.setVisibility(value >= 100 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (title != null && !title.trim().isEmpty()) toolbarTitle.setText(title);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView popup = new WebView(WebContentActivity.this);
                popup.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView ignored, WebResourceRequest request) {
                        return openExternal(request.getUrl());
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView ignored, String url) {
                        return openExternal(Uri.parse(url));
                    }
                });
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(popup);
                resultMsg.sendToTarget();
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return routeUrl(request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return routeUrl(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                errorPanel.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) showLoadError();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, android.webkit.WebResourceResponse response) {
                if (request.isForMainFrame() && response.getStatusCode() >= 500) showLoadError();
            }
        });
        content.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        errorPanel = new TextView(this);
        errorPanel.setText("Impossible de charger cette page.\nVérifie ta connexion puis touche ici pour réessayer.");
        errorPanel.setTextSize(15);
        errorPanel.setTextColor(NAVY);
        errorPanel.setGravity(Gravity.CENTER);
        errorPanel.setPadding(dp(24), dp(24), dp(24), dp(24));
        errorPanel.setBackgroundColor(WHITE);
        errorPanel.setVisibility(View.GONE);
        errorPanel.setOnClickListener(v -> {
            errorPanel.setVisibility(View.GONE);
            webView.loadUrl(webView.getUrl() == null ? initialUrl : webView.getUrl());
        });
        content.addView(errorPanel, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        root.addView(content, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);
        webView.loadUrl(initialUrl);
    }

    private boolean routeUrl(String value) {
        if (isWebUrl(value)) {
            Uri uri = Uri.parse(value);
            String path = uri.getPath() == null ? "" : uri.getPath().toLowerCase();
            if (path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".docx")) {
                return openExternal(uri);
            }
            return false;
        }
        if (value != null && value.startsWith("intent:")) {
            try {
                Intent intent = Intent.parseUri(value, Intent.URI_INTENT_SCHEME);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    return true;
                }
                String fallback = intent.getStringExtra("browser_fallback_url");
                if (isWebUrl(fallback)) {
                    webView.loadUrl(fallback);
                    return true;
                }
            } catch (Exception ignored) {}
            Toast.makeText(this, "Cette destination n’est pas disponible sur ce téléphone.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return openExternal(Uri.parse(value == null ? "" : value));
    }

    private boolean openExternal(Uri uri) {
        try {
            if (uri == null || uri.getScheme() == null) return true;
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Aucune application ne peut ouvrir ce lien.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Impossible d’ouvrir ce lien.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean isWebUrl(String value) {
        if (value == null) return false;
        return value.startsWith("https://") || value.startsWith("http://");
    }

    private void showLoadError() {
        errorPanel.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void openInBrowser() {
        String url = webView == null ? initialUrl : webView.getUrl();
        if (!isWebUrl(url)) url = initialUrl;
        openExternal(Uri.parse(url));
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
