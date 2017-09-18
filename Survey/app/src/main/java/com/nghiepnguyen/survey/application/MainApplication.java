package com.nghiepnguyen.survey.application;

import android.app.Application;
import android.content.Context;

import com.nghiepnguyen.survey.Interface.NetComponent;
import com.nghiepnguyen.survey.networking2.NetModule;

/**
 * Created by Nghiep Nguyen on 10/29/15.
 */
public class MainApplication extends Application {
    private static MainApplication instance;
    private NetComponent mNetComponent;

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
        mNetComponent = DaggerNetComponent.builder()
                // list of modules that are part of this component need to be created here too
                .netModule(new NetModule())
                .build();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }

}
