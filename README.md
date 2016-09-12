# Android AppVersionChecker [![](https://jitpack.io/v/sugoi-wada/AppVersionChecker.svg)](https://jitpack.io/#sugoi-wada/AppVersionChecker)

An Android library that checks for your application's updates on Google Play Store. This library uses Android Publisher API.

## Requirements

- Minimum Android API level 15 to use
- RxJava 1.1.x

## Getting Started

1. Upload the first version of your APK using the web interface.
1. Create a Google Play Service Account and download key JSON file(see [Service Account](#service-account)).
1. Copy the key JSON file to application `assets` directory.
1. Add the library using JitPack(see [Usage](#usage)).
1. Call the method of `PlayStore.checkForUpdates`

## Service Account

To use this library you have to create a service account for your existing Google Play Account. See [here](https://developers.google.com/android-publisher/getting_started#using_a_service_account) and create account. Make sure to be downloaded key JSON file.

Make sure that revoke all permissions and only grant `read only` role.

## Usage

Add the JitPack repository to your build file:

```
allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```

Add the dependency to your application module:

```
dependencies {
    compile 'com.github.sugoi-wada:appversionchecker:v0.1.2'
}
```

You can usually call the method like:

```
PlayStore.checkForUpdates(context, jsonAssetsFileName);
```

If you use alpha/beta release:

```
PlayStore.checkForUpdates(context, jsonAssetsFileName, PlayStore.ReleaseType.BETA);
PlayStore.checkForUpdates(context, jsonAssetsFileName, PlayStore.ReleaseType.ALPHA);
```

## License

Copyright (c) 2016 sugoi_wada.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.