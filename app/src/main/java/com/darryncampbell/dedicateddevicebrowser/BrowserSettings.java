package com.darryncampbell.dedicateddevicebrowser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class BrowserSettings {

    private static final String LOG_TAG = "DDBrowser";
    private boolean shouldLoadPageOnLaunch = true;

    private static final String key_start_page = "start_page";
    private static final String key_lock_task_mode = "lock_task_mode";

    //  Configurable items
    private String startPage = "http://www.google.com";
    private boolean startLockTaskMode = false;

    public void resolveRestrictions(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            RestrictionsManager manager =
                    (RestrictionsManager) context.getSystemService(Context.RESTRICTIONS_SERVICE);
            Bundle restrictions = null;
            restrictions = manager.getApplicationRestrictions();
            List<RestrictionEntry> entries = manager.getManifestRestrictions(
                    context.getApplicationContext().getPackageName());
            for (RestrictionEntry entry : entries) {
                String key = entry.getKey();
                Log.d(LOG_TAG, "key: " + key);
                if (key.equals(key_start_page)) {
                    if (restrictions != null && restrictions.containsKey(key_start_page))
                    {
                        String newStartPage = restrictions.getString(key_start_page);
                        ProcessStartPage(newStartPage);
                    }
                }
                else if (key.equals(key_lock_task_mode))
                {
                    if (restrictions != null && restrictions.containsKey(key_lock_task_mode))
                    {
                        boolean lockTaskMode = restrictions.getBoolean(key_lock_task_mode);
                        startLockTaskMode = lockTaskMode;
                    }
                }
                //  todo other configuration items from managed configuration e.g. disable back button
            }
        }
    }

    public void ProcessStartPage(String newStartPage)
    {
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

    public boolean configurationFileExists()
    {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File configurationFile = new File(path, "dedicated_browser_configuration.json");
        if (configurationFile.exists())
            return true;
        else
            return false;
    }

    public void loadFileBasedConfiguration(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.w(LOG_TAG, "Application does not have storage permission.  File based configuration not available");
            }
        }
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File configurationFile = new File(path, "dedicated_browser_configuration.json");
        if (!configurationFile.exists())
        {
            Log.w(LOG_TAG, "Unable to load dedicated_browser_configuration.json from the Documents folder");
            return;
        }
        StringBuilder text = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(configurationFile));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch(IOException e)
        {
            Log.e(LOG_TAG, "Unable to read configuration file: " + e.getMessage());
        }
        try {
            JSONObject obj = new JSONObject(text.toString());
            if (obj.has(key_start_page))
            {
                ProcessStartPage(obj.getString(key_start_page));
            }
            if (obj.has(key_lock_task_mode))
            {
                boolean lockTaskMode = obj.getBoolean(key_lock_task_mode);
                startLockTaskMode = lockTaskMode;
            }
            //  todo process any more file-based configuration options
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Unable to parse configuration file: " + e.getMessage());
        }
    }


    public String getStartPage() {
        return startPage;
    }

    public boolean getStartLockTaskMode() {
        return startLockTaskMode;
    }

    public void setShouldLoadPageOnLaunch(boolean b) {
        shouldLoadPageOnLaunch = b;
    }

    public boolean getShouldLoadPageOnLaunch() {
        return shouldLoadPageOnLaunch;
    }

    public boolean fileBasedConfigurationAllowed() {
        //  todo
        return true;
    }

}
