package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nghiep on 11/22/15.
 */
public class ProjectModel implements Parcelable{
    private int ID;
    private String Code;
    private String Name;
    private String Summary;
    private String Description;
    private String Image1;
    private String Image2;
    private String Image3;
    private String Image4;

    protected ProjectModel(Parcel in) {
        ID = in.readInt();
        Code = in.readString();
        Name = in.readString();
        Summary = in.readString();
        Description = in.readString();
        Image1 = in.readString();
        Image2 = in.readString();
        Image3 = in.readString();
        Image4 = in.readString();
    }

    public static final Creator<ProjectModel> CREATOR = new Creator<ProjectModel>() {
        @Override
        public ProjectModel createFromParcel(Parcel in) {
            return new ProjectModel(in);
        }

        @Override
        public ProjectModel[] newArray(int size) {
            return new ProjectModel[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

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

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getImage3() {
        return Image3;
    }

    public void setImage3(String image3) {
        Image3 = image3;
    }

    public String getImage4() {
        return Image4;
    }

    public void setImage4(String image4) {
        Image4 = image4;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(Code);
        parcel.writeString(Name);
        parcel.writeString(Summary);
        parcel.writeString(Description);
        parcel.writeString(Image1);
        parcel.writeString(Image2);
        parcel.writeString(Image3);
        parcel.writeString(Image4);
    }
}
