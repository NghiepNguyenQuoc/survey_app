package com.nghiepnguyen.survey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.storage.UserInfoManager;

/**
 * Created by nghiep on 11/22/15.
 */
public class SplashScreenActivity extends BaseActivity {

    private long startSplashscreenTime = 0;

    private static final int SPLASHSCREEN_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                checkLogin();
            }
        }, SPLASHSCREEN_TIMEOUT);

    }

    private void checkLogin() {
        UserInfoModel user = UserInfoManager.getUserInfo(this);
        Intent intent;
        if (user != null) {
            intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
