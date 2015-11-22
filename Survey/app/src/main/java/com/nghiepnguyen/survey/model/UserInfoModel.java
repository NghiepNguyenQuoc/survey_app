package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by 08670_000 on 22/11/2015.
 */
public class UserInfoModel implements Parcelable {
    private String SecrectToken;
    private int ID;
    private String FullName;
    private String LoginName;

    public String getSecrectToken() {
        return SecrectToken;
    }

    public void setSecrectToken(String secrectToken) {
        SecrectToken = secrectToken;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
