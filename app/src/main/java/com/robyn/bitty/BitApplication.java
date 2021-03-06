package com.robyn.bitty;

import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.robyn.bitty.utils.MakeSoundKt.playSound;

/**
 * Created by yifei on 7/17/2017.
 */

public class BitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // todo comment out
        playSound(this);

        // Auth configuration
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig
                        (BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);

        // Setup OkHttp client
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        final OkHttpClient customClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor).build();

        // Setup twitter api client
        final TwitterSession activeSession = TwitterCore.getInstance()
                .getSessionManager().getActiveSession();

        final TwitterApiClient customApiClient;

        if (activeSession != null) {
            customApiClient = new TwitterApiClient(activeSession, customClient);
            TwitterCore.getInstance().addApiClient(activeSession, customApiClient);
        } else {
            customApiClient = new TwitterApiClient(customClient);
            TwitterCore.getInstance().addGuestApiClient(customApiClient);
        }
    }
}

