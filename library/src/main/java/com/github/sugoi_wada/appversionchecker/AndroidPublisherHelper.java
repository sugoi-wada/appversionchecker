package com.github.sugoi_wada.appversionchecker;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;

import java.io.IOException;
import java.util.Collections;

public class AndroidPublisherHelper {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;

    public static AndroidPublisher init(Context context, String fileName) throws IOException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(context.getPackageName()), "applicationId cannot be null or empty!");
        newTrustedTransport();
        GoogleCredential credential = GoogleCredential.fromStream(context.getAssets().open(fileName), HTTP_TRANSPORT, JSON_FACTORY);
        credential = credential.createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));

        return new AndroidPublisher.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(context.getPackageName())
                .build();
    }

    private static void newTrustedTransport() {
        if (HTTP_TRANSPORT == null) {
            HTTP_TRANSPORT = new NetHttpTransport();
        }
    }
}
