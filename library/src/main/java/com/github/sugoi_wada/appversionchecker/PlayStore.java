package com.github.sugoi_wada.appversionchecker;

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.androidpublisher.model.ApkListing;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;

public class PlayStore {

    public enum ReleaseType {
        PRODUCTION, BETA, ALPHA;
    }

    public static Observable<AppListing> checkForUpdates(Context context, String jsonAssetsFileName) {
        return checkForUpdates(context, jsonAssetsFileName, ReleaseType.PRODUCTION);
    }

    public static Observable<AppListing> checkForUpdates(Context context, String jsonAssetsFileName, ReleaseType releaseType) {
        return checkForUpdates(context, context.getPackageName(), jsonAssetsFileName, releaseType);
    }

    private static Observable<AppListing> checkForUpdates(final Context context, final String packageName, final String jsonAssetsFileName, final ReleaseType releaseType) {
        return Single.create(new Single.OnSubscribe<AppListing>() {
            @Override
            public void call(SingleSubscriber<? super AppListing> singleSubscriber) {
                try {
                    StoreApi storeApi = new StoreApi();
                    storeApi.init(context, packageName, jsonAssetsFileName);
                    int latestVersionCode = storeApi.latestVersionCode(releaseType);
                    int appVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
                    if (appVersionCode >= latestVersionCode) {
                        // No updates.
                        if (!singleSubscriber.isUnsubscribed()) {
                            singleSubscriber.onSuccess(null);
                        }
                        return;
                    }

                    AppListing appListing = new AppListing();
                    appListing.setVersionCode(latestVersionCode);
                    appListing.setReleaseType(releaseType);

                    List<ApkListing> listings = storeApi.apkListings(latestVersionCode);
                    if (listings == null || listings.isEmpty()) {
                        if (!singleSubscriber.isUnsubscribed()) {
                            singleSubscriber.onSuccess(appListing);
                        }
                        return;
                    }

                    appListing.setDefaultLanguage(listings.get(0).getLanguage());
                    appListing.setDefaultRecentChanges(listings.get(0).getRecentChanges());

                    String bcp47Language = LocaleUtil.toBcp47Language(Locale.getDefault());
                    for (ApkListing listing : listings) {
                        if (listing.getLanguage().equals(bcp47Language)) {
                            appListing.setLanguage(listing.getLanguage());
                            appListing.setRecentChanges(listing.getRecentChanges());
                            break;
                        }
                    }
                    if (!singleSubscriber.isUnsubscribed()) {
                        singleSubscriber.onSuccess(appListing);
                    }
                } catch (IOException | PackageManager.NameNotFoundException e) {
                    if (e instanceof GoogleJsonResponseException && ((GoogleJsonResponseException) e).getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                        singleSubscriber.onSuccess(null);
                        return;
                    }
                    if (!singleSubscriber.isUnsubscribed()) {
                        singleSubscriber.onError(e);
                        return;
                    }
                }
            }
        }).toObservable();
    }
}
