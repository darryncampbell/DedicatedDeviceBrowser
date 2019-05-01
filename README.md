*This application is provided without guarantee or warranty*
=========================================================
# Dedicated Device Browser
A very simple locked down browser for dedicated devices based on Chrome webview.  This browser will not expose a URL bar or any other controls to the user.  Browser configuration is handled by a device administrator, either by using Managed Configurations [AKA application restrictions] (recommended) or by copying a json file to the device.

## Configuration
The Dedicated Device Browser can be configured in a few ways:

- (Recommended) Android Managed Configurations, controlled by a Device Owner (EMM).  You can use [TestDPC](https://github.com/googlesamples/android-testdpc) or the [Android Management Experience](https://enterprise.google.com/android/experience) to test this without an EMM
- By copying a .json file to the device mass storage Documents folder.  /[public external storage]/Documents/dedicated_browser_configuration.json

### Available configuration
| Option | Description|
|--------|---------|
| Start Page| The start URL of the browser|
| Allow file-based configuration| Whether or not the browser will accept the .json file containing configuration.  Disabling this option avoids having to worry about a nefarious operator causing unwanted configuration.  This option is only available via Managed Configuration. |
|Lock Task Mode| On Marshmallow, Nougat and Oreo an application had to request that it be put into [lock task mode](https://developer.android.com/work/dpc/dedicated-devices/lock-task-mode).  When true, the browser will request lock task mode.  The DO also needs to whitelist the application otherwise the browser will just be 'pinned'.  On Pie+ the EMM is able to put ANY application into lock task mode so this option is not necessary |
| Ignore SSL Errors | When true, the browser will navigate to a page in spite of SSL errors.  Do not use this setting in production |

## Locking down the device

The Dedicated Device browser will run in full screen but to prevent the user from exiting the application or interacting with the home screen / back button etc it is necessary to lock down the device in one of the following ways:

- Using Android's [lock task mode](https://developer.android.com/work/dpc/dedicated-devices/lock-task-mode) which can be initiated by whitelisting the application via an EMM 
- Using a custom launcher or custom home screen (many EMMs will also provide this type of utility)
- Using OEM-specific tools and utilities to lock the device down.  For example Zebra offer "Mobility Extensions" as detailed [here](https://developer.zebra.com/community/home/blog/2017/04/10/locking-down-your-device)

## Extensibility

This application is designed to show the bare-bones to getting started with a browser for dedicated devices.  There are a number of ways it could be improved:

- The custom webView client, based on Android's [WebViewClient class](https://developer.android.com/reference/android/webkit/WebViewClient) has many other overrides which can provide enhanced functionality.  Currently the only custom functionality implemented here is the ability to ignore SSL errors and logging page load errors
-  Android's [webView](https://developer.android.com/reference/android/webkit/WebView) class has a lot more configuration which could be exposed for example clearing history, safe browsing, disabling zoom or disabling the back button (through the key event handler).

## Screenshots

![BBC.co.uk](https://raw.githubusercontent.com/darryncampbell/DedicatedDeviceBrowser/master/screenshots/bbc.jpg)

www.bbc.co.uk shown in the browser

![Google.co.uk](https://raw.githubusercontent.com/darryncampbell/DedicatedDeviceBrowser/master/screenshots/google.jpg)

www.google.co.uk shown in the browser

![Test DPC](https://raw.githubusercontent.com/darryncampbell/DedicatedDeviceBrowser/master/screenshots/testdpc.jpg)

Available managed configuration options as displayed through [TestDPC](https://github.com/googlesamples/android-testdpc)

## Videos

[![Specifying the Start page](http://img.youtube.com/vi/lcIIVZ4P2uo/0.jpg)](https://youtu.be/lcIIVZ4P2uo) 

Specifying a custom start page

[![Specifying the Start page](http://img.youtube.com/vi/aqKmxKxvEQg/0.jpg)](https://youtu.be/aqKmxKxvEQg) 

Enabling lock task mode to prevent user access to the OS