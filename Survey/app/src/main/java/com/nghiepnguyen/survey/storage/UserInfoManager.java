package com.nghiepnguyen.survey.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nghiepnguyen.survey.application.MainApplication;
import com.nghiepnguyen.survey.model.MemberModel;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.utils.Constant;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by nghiep on 11/22/15.
 */
public class UserInfoManager {
    private static final String TAG = UserInfoManager.class.getSimpleName();


    public static void saveUserInfo(Context mContext, UserInfoModel userInfo) {
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

    public static void saveMemberInfo(Context mContext, MemberModel memberModel) {
        SharedPreferences preferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        String serializedUser = gson.toJson(memberModel);
        editor.putString(Constant.SHARED_PREFERENCE_CURRENT_MEMBER, serializedUser);
        editor.apply();
    }

    public static MemberModel getMemberInfo(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String serializedUser = preferences.getString(Constant.SHARED_PREFERENCE_CURRENT_MEMBER, "");
        MemberModel memberModel = null;
        if (!TextUtils.isEmpty(serializedUser)) {
            Gson gson = new Gson();
            memberModel = gson.fromJson(serializedUser, MemberModel.class);
        }
        return memberModel;
    }

    public static void saveUploadTimesOfProject(Context context, Map<String, Integer> inputMap) {
        SharedPreferences idPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        if (idPrefs != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = idPrefs.edit();
            editor.remove(Constant.SHARED_PREFERENCE_NUMBER_UPLOAD_SUCCESSFULLY).commit();
            editor.putString(Constant.SHARED_PREFERENCE_NUMBER_UPLOAD_SUCCESSFULLY, jsonString);
            editor.commit();
        }
    }

    public static Map<String, Integer> getUploadTimesOfProject(Context context) {
        Map<String, Integer> outputMap = new HashMap<>();
        SharedPreferences idPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        try {
            if (idPrefs != null) {
                String jsonString = idPrefs.getString(Constant.SHARED_PREFERENCE_NUMBER_UPLOAD_SUCCESSFULLY, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String k = keysItr.next();
                    Integer v = (Integer) jsonObject.get(k);
                    outputMap.put(k, v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }
}
