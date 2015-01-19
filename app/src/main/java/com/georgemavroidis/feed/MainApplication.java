package com.georgemavroidis.feed;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;

/**
 * Created by george on 14-11-09.
 */
public class MainApplication extends Application {
    private static MainApplication instance = new MainApplication();

    public MainApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        Parse.initialize(this, "3GfVj2RwJM2JugKccbstKFgXJZfr2R0wSU17ApCF", "rYHWr6B4zCLi0hh4TE7F6Vpvf63Iy4QING6TgRAK");

        // Also in this method, specify a default Activity to handle push notifications
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("global");

    }


}