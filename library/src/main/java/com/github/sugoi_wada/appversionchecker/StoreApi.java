package com.github.sugoi_wada.appversionchecker;

import android.content.Context;

import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.ApkListing;
import com.google.api.services.androidpublisher.model.ApkListingsListResponse;
import com.google.api.services.androidpublisher.model.Track;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StoreApi {

    private String packageName;
    private String id;
    private AndroidPublisher.Edits edits;

    public StoreApi() {
    }

    public void init(Context context, String packageName, String jsonAssetsFileName) throws IOException {
        this.packageName = packageName;
        AndroidPublisher publisher = AndroidPublisherHelper.init(context, jsonAssetsFileName);
        edits = publisher.edits();
        id = edits.insert(packageName, null).execute().getId();
    }

    public int latestVersionCode(PlayStore.ReleaseType releaseType) throws IOException {
        Track track = edits.tracks().get(packageName, id, releaseType.name().toLowerCase(Locale.US)).execute();
        List<Integer> versionCodes = track.getVersionCodes();

        if (versionCodes == null || versionCodes.isEmpty()) {
            return -1;
        }

        // order by desc
        Collections.sort(versionCodes, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs.compareTo(lhs);
            }
        });
        return versionCodes.get(0);
    }

    public List<ApkListing> apkListings(PlayStore.ReleaseType releaseType) throws IOException {
        return apkListings(latestVersionCode(releaseType));
    }

    public List<ApkListing> apkListings(int versionCode) throws IOException {
        ApkListingsListResponse apkListingsList = edits.apklistings().list(packageName, id, versionCode).execute();
        return apkListingsList.getListings();
    }
}
