*This application is provided without guarantee or warranty*
=========================================================
# Dedicated Device Browser
Locked down browser for dedicated devices based on Chrome webview

## Configuration
The Dedicated Device Browser can be configured in a few ways:

- (Recommended) Android Managed Configurations, controlled by a Device Owner (EMM) on the device.
- (not implemented) By copying a .json file to the device mass storage

### Available configuration
- StartPage
- (not implemented) EnableFileBasedConfiguration (load from json file)
- LockTaskMode (Marshmallow and higher)

## Locking down the device

The Dedicated Device browser will run in full screen, to prevent the user from exiting the application or interacting with the home screen / back button etc. it is necessary to lock down the device in one of the following ways:

- Using Android's LockTaskMode which can be initiated by whitelisting the application via an EMM 
- Using a custom launcher or custom home screen (many EMMs will also provide this type of utility)
- Using EMM-specific tools and utilities to lock the device down.
