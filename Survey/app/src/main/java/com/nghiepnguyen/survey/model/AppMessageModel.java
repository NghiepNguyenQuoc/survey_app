package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 08670_000 on 27/02/2016.
 */
public class AppMessageModel implements Parcelable {
    private String Code;
    private String Name;
    private String Description;
    private String Result;
    private boolean IsSuccessfull;
    private int Type;
    private int ID;

    public AppMessageModel(Parcel in) {
        Code = in.readString();
        Name = in.readString();
        Description = in.readString();
        Result = in.readString();
        IsSuccessfull = in.readByte() != 0;
        Type = in.readInt();
        ID = in.readInt();
    }

    public static final Creator<AppMessageModel> CREATOR = new Creator<AppMessageModel>() {
        @Override
        public AppMessageModel createFromParcel(Parcel in) {
            return new AppMessageModel(in);
        }

        @Override
        public AppMessageModel[] newArray(int size) {
            return new AppMessageModel[size];
        }
    };

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public boolean isSuccessfull() {
        return IsSuccessfull;
    }

    public void setIsSuccessfull(boolean isSuccessfull) {
        IsSuccessfull = isSuccessfull;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Code);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeString(Result);
        dest.writeByte((byte) (IsSuccessfull ? 1 : 0));
        dest.writeInt(Type);
        dest.writeInt(ID);
    }
}
