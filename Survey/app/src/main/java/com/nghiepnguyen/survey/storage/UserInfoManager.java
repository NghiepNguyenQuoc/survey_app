package com.nghiepnguyen.survey.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.nghiepnguyen.survey.application.MainApplication;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.utils.Constant;

/**
 * Created by nghiep on 11/22/15.
 */
public class UserInfoManager {
    private static final String TAG = UserInfoManager.class.getSimpleName();


    public static void saveUserInfo(Context mContext,UserInfoModel userInfo) {
        SharedPreferences preferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        String serializedUser = gson.toJson(userInfo);
        editor.putString(Constant.SHARED_PREFERENCE_CURRENT_USER, serializedUser);
        editor.apply();
    }
    public static UserInfoModel getUserInfo(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String serializedUser = preferences.getString(Constant.SHARED_PREFERENCE_CURRENT_USER, "");
        UserInfoModel userInfo = null;
        if (!TextUtils.isEmpty(serializedUser)) {
            Gson gson = new Gson();
            userInfo = gson.fromJson(serializedUser, UserInfoModel.class);
        }
        return userInfo;
    }
}
