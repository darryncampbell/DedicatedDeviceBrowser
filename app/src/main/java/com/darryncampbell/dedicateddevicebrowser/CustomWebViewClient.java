package com.darryncampbell.dedicateddevicebrowser;

import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import android.webkit.WebViewClient;

//  https://developer.android.com/reference/android/webkit/WebViewClient
public class CustomWebViewClient extends WebViewClient {

    private static final String LOG_TAG = "DDBrowser";
    private boolean ignoreSSLErrors;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //  We want this application to handle all URLs
        return false;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        //  Test with https://badssl.com
        if (ignoreSSLErrors)
            handler.proceed(); // Ignore SSL certificate errors
        else
            handler.cancel();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.e(LOG_TAG, "Error navigating to " + failingUrl + " [" + errorCode + "]");
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(LOG_TAG, "Error navigating to " + request.getUrl().toString() + " [" + error.getErrorCode() + "]");
        }
    }

    public void setIgnoreSSLErrors(boolean ignoreSSLErrors) { this.ignoreSSLErrors = ignoreSSLErrors; }
}
