package com.darryncampbell.dedicateddevicebrowser;

import android.webkit.WebView;

import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //  We want this application to handle all URLs
        return false;
    }
}
