package com.nghiepnguyen.survey.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by nghiep on 10/29/15.
 */
public class MainApplication extends Application {

    private static MainApplication instance;

    public MainApplication() {
        super();
        instance = this;
    }

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Foreground.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


}
