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

    protected UserInfoModel(Parcel in) {
        SecrectToken = in.readString();
        ID = in.readInt();
        FullName = in.readString();
        LoginName = in.readString();
    }

    public static final Creator<UserInfoModel> CREATOR = new Creator<UserInfoModel>() {
        @Override
        public UserInfoModel createFromParcel(Parcel in) {
            return new UserInfoModel(in);
        }

        @Override
        public UserInfoModel[] newArray(int size) {
            return new UserInfoModel[size];
        }
    };

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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(SecrectToken);
        parcel.writeInt(ID);
        parcel.writeString(FullName);
        parcel.writeString(LoginName);
    }
}
