package com.darryncampbell.dedicateddevicebrowser;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;

import java.util.List;

public class FullscreenActivity extends AppCompatActivity {
    private View mContentView;
    private WebView myWebView;
    private CustomWebViewClient customWebViewClient;
    // Observes restriction changes
    private BroadcastReceiver mBroadcastReceiver;
    private BrowserSettings browserSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        browserSettings = new BrowserSettings();
        mContentView = findViewById(R.id.webview);
        myWebView = (WebView) findViewById(R.id.webview);
        customWebViewClient = new CustomWebViewClient();
        myWebView.setWebViewClient(customWebViewClient);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //  Register to receive changes to the managed configurations
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                browserSettings.resolveRestrictions(getApplicationContext());
            }
        };
        registerReceiver(mBroadcastReceiver,
                new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        showAsFullScreen();
        browserSettings.resolveRestrictions(this);
        //  File based settings
        if (browserSettings.fileBasedConfigurationAllowed() && browserSettings.configurationFileExists())
        {
            //  Load file based configuration
            browserSettings.loadFileBasedConfiguration(this);
        }
        //  End file based settings

        //  Apply any settings which have changed since the last launch
        customWebViewClient.setIgnoreSSLErrors(browserSettings.getShouldIgnoreSSLErrors());
        if (browserSettings.getShouldLoadPageOnLaunch())
            myWebView.loadUrl(browserSettings.getStartPage());
        browserSettings.setShouldLoadPageOnLaunch(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (browserSettings.getStartLockTaskMode())
                startLockTask();
            else
                stopLockTask();
        }
        //  End apply any settings which have changed since the last launch
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //  Unregister the managed configurations broadcast receiver
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    private void showAsFullScreen() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}




