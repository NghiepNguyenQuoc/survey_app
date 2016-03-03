package com.nghiepnguyen.survey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.MemberModel;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.storage.UserInfoManager;
import io.fabric.sdk.android.Fabric;

/**
 * Created by nghiep on 11/22/15.
 */
public class SplashScreenActivity extends BaseActivity {

    private long startSplashscreenTime = 0;

    private static final int SPLASHSCREEN_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                checkLogin();
            }
        }, SPLASHSCREEN_TIMEOUT);

    }

    private void checkLogin() {
        //UserInfoModel user = UserInfoManager.getUserInfo(this);
        MemberModel member = UserInfoManager.getMemberInfo(this);
        Intent intent;
        if (member != null) {
            intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            //intent = new Intent(SplashScreenActivity.this, Info_Interview.class);
        }
        startActivity(intent);
        finish();
    }

}
