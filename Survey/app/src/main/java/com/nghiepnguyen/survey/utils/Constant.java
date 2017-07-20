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

    // The minimum distance to change Updates in meters
    public static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    public static final int MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    /*activity*/
    public static final String FORGET_PASSWORD = "ForgetPasswordActivity";

    /*fragment*/
    public static final String PROJECT_LIST = "ProjectListFragment";


    /*bundle*/
    public static final String BUNDLE_QUESTION = "BUNDLE_QUESTION";
    public static final String BUNDLE_QUESTIONNAIRE = "BUNDLE_QUESTIONNAIRE";

    public static final String QUESTION_ID = "QUESTION_ID";

    public static final String SHARED_PREFERENCE_CURRENT_USER = "SHARED_PREFERENCE_CURRENT_USER";
    public static final String SHARED_PREFERENCE_CURRENT_MEMBER = "SHARED_PREFERENCE_CURRENT_MEMBER";
    public static final String SHARED_PREFERENCE_NUMBER_UPLOAD_SUCCESSFULLY = "SHARED_PREFERENCE_NUMBER_UPLOAD_SUCCESSFULLY";

    public static final String FORMAT_24_HOURS_DAY = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_24_HOURS_DAY_SHORT = "yyyyMMddHHmmss";

    // Convert dp to px
    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = (int) (dp * displayMetrics.density + 0.5);
        return px;
    }
}
