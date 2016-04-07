package com.github.sugoi_wada.sample;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.github.sugoi_wada.appversionchecker.PlayStore;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscription = PlayStore.checkForUpdates(this, "Google-Play-Android-Developer.json", PlayStore.ReleaseType.BETA)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    String msg;
                    if (s == null) {
                        msg = "No updates.";
                    } else {
                        msg = s.toString();
                    }
                    new AlertDialog.Builder(this)
                            .setMessage(msg)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }, Throwable::printStackTrace);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
