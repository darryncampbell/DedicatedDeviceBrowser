package com.darryncampbell.dedicateddevicebrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.os.Build;
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
    // Observes restriction changes
    private BroadcastReceiver mBroadcastReceiver;
    private static final String LOG_TAG = "DDBrowser";
    private boolean shouldLoadPageOnLaunch = true;

    private static final String key_start_page = "key_start_page";
    private static final String key_lock_task_mode = "key_lock_task_mode";

    //  Configurable items
    private String startPage = "http://www.google.com";
    private boolean startLockTaskMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mContentView = findViewById(R.id.webview);
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new CustomWebViewClient());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //  Register to receive changes to the managed configurations
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                resolveRestrictions();
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
        resolveRestrictions();
        if (shouldLoadPageOnLaunch)
            myWebView.loadUrl(startPage);
        shouldLoadPageOnLaunch = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (startLockTaskMode)
                startLockTask();
        }

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

    private void resolveRestrictions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            RestrictionsManager manager =
                    (RestrictionsManager) getSystemService(Context.RESTRICTIONS_SERVICE);
            Bundle restrictions = null;
            restrictions = manager.getApplicationRestrictions();
            List<RestrictionEntry> entries = manager.getManifestRestrictions(
                    getApplicationContext().getPackageName());
            for (RestrictionEntry entry : entries) {
                String key = entry.getKey();
                Log.d(LOG_TAG, "key: " + key);
                if (key.equals(key_start_page)) {
                    if (restrictions != null && restrictions.containsKey(key_start_page))
                    {
                        String newStartPage = restrictions.getString(key_start_page);
                        if (!(newStartPage.startsWith("http:") || newStartPage.startsWith("https:")))
                            newStartPage = "http://" + newStartPage;
                        if (URLUtil.isValidUrl(newStartPage))
                        {
                            if (!startPage.equals(newStartPage))
                            {
                                shouldLoadPageOnLaunch = true;
                                startPage = newStartPage;
                            }
                        }
                        else
                        {
                            Log.w(LOG_TAG, "Invalid URL specified as start page: " + newStartPage);
                        }
                    }
                }
                else if (key.equals(key_lock_task_mode))
                {
                    if (restrictions != null && restrictions.containsKey(key_lock_task_mode))
                    {
                        boolean lockTaskMode = restrictions.getBoolean(key_lock_task_mode);
                        startLockTaskMode = lockTaskMode;
                        if (!lockTaskMode)
                            stopLockTask();
                    }
                }
                //  todo other configuration items e.g. disable back button
            }
        }
    }

}




