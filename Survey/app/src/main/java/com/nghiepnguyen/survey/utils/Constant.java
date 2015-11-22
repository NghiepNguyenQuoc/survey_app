package com.nghiepnguyen.survey.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by nghiep on 10/29/15.
 */
public class Constant {

    public static final int CODE_CANNOT_CONNECT_INTERNET = 4004;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int KEY_ACTIVITY_RESULT_SIGN_IN = 47;


    public static final String FORGET_PASSWORD = "ForgetPasswordActivity";
    public static final String SHARED_PREFERENCE_CURRENT_USER = "CURRENT USER INFO";
    // Convert dp to px
    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = (int) (dp * displayMetrics.density + 0.5);
        return px;
    }
}
