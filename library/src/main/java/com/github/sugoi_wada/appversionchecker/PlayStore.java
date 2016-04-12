package com.github.sugoi_wada.appversionchecker;

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.ApkListing;
import com.google.api.services.androidpublisher.model.ApkListingsListResponse;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Track;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Single;

/**
 * Created by watyaa on 16/04/06.
 */
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

    private static Observable<AppListing> checkForUpdates(Context context, String packageName, String jsonAssetsFileName, ReleaseType releaseType) {
        return Single.create((Single.OnSubscribe<AppListing>) singleSubscriber -> {
            AndroidPublisher publisher = null;
            try {
                publisher = AndroidPublisherHelper.init(context, jsonAssetsFileName);
                AndroidPublisher.Edits edits = publisher.edits();
                AppEdit appEdit = edits.insert(packageName, null).execute();

                Track track = edits.tracks().get(packageName, appEdit.getId(), releaseType.name().toLowerCase(Locale.US)).execute();
                List<Integer> versionCodes = track.getVersionCodes();
                if (versionCodes == null || versionCodes.isEmpty()) {
                    // No updates
                    if (!singleSubscriber.isUnsubscribed()) {
                        singleSubscriber.onSuccess(null);
                    }
                    return;
                }

                // order by desc
                Collections.sort(versionCodes, (lhs, rhs) -> rhs.compareTo(lhs));
                int latestVersionCode = versionCodes.get(0);
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

                ApkListingsListResponse apkListingsList = edits.apklistings().list(packageName, appEdit.getId(), latestVersionCode).execute();
                List<ApkListing> listings = apkListingsList.getListings();

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
        }).toObservable();
    }
}
