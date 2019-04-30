package com.darryncampbell.dedicateddevicebrowser;

import android.app.Activity;
import android.content.Context;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;

import java.util.List;

public class BrowserSettings {

    private static final String LOG_TAG = "DDBrowser";
    private boolean shouldLoadPageOnLaunch = true;

    private static final String key_start_page = "key_start_page";
    private static final String key_lock_task_mode = "key_lock_task_mode";

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
                    }
                }
                //  todo other configuration items e.g. disable back button
            }
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
}
