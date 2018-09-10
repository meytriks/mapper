# mapper
Android location-based crowdsourcing application.

![screenshot_home](meta/Screenshot_2018-09-08-15-53-57.png "screenshot") ![screenshot_navigation](meta/Screenshot_2018-09-08-15-59-30.png  "screenshot") ![screenshot_new](meta/Screenshot_2018-09-08-15-56-57.png "screenshot") ![screenshot_marker](meta/Screenshot_2018-09-08-15-57-24.png "screenshot") ![screenshot_map](meta/Screenshot_2018-09-08-15-58-57.png "screenshot") ![screenshot_discover](meta/Screenshot_2018-09-08-16-00-30.png "screenshot")

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone https://github.com/dusandjovanovic/mapper.git
```

## Configuration
### Keystores:
Create `app/keystore.gradle` with the following info:
```gradle
ext.key_alias='...'
ext.key_password='...'
ext.store_password='...'
```
And place both keystores under `app/keystores/` directory:
- `playstore.keystore`
- `stage.keystore`


## Build variants
Use the Android Studio *Build Variants* button to choose between **production** and **staging** flavors combined with debug and release build types


## Generating signed APK
From Android Studio:
1. ***Build*** menu
2. ***Generate Signed APK...***
3. Fill in the keystore information *(you only need to do this once manually and then let Android Studio remember it)*

