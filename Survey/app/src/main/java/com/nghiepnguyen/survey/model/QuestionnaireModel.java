package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nghiep on 2/10/16.
 */
public class QuestionnaireModel implements Parcelable {
    private int ID;
    private int QuestionnaireID;
    private int AllowInputText;
    private int Type;
    private int Value;
    private String Description;
    private String Caption;
    private boolean isSelected;
    private String otherOption;

    protected QuestionnaireModel(Parcel in) {
        ID = in.readInt();
        QuestionnaireID = in.readInt();
        AllowInputText = in.readInt();
        Type = in.readInt();
        Value = in.readInt();
        Description = in.readString();
        Caption = in.readString();
        otherOption = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<QuestionnaireModel> CREATOR = new Creator<QuestionnaireModel>() {
        @Override
        public QuestionnaireModel createFromParcel(Parcel in) {
            return new QuestionnaireModel(in);
        }

        @Override
        public QuestionnaireModel[] newArray(int size) {
            return new QuestionnaireModel[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getQuestionnaireID() {
        return QuestionnaireID;
    }

    public void setQuestionnaireID(int questionnaireID) {
        QuestionnaireID = questionnaireID;
    }

    public int getAllowInputText() {
        return AllowInputText;
    }

    public void setAllowInputText(int allowInputText) {
        AllowInputText = allowInputText;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getOtherOption() {
        return otherOption;
    }

    public void setOtherOption(String otherOption) {
        this.otherOption = otherOption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeInt(QuestionnaireID);
        parcel.writeInt(AllowInputText);
        parcel.writeInt(Type);
        parcel.writeInt(Value);
        parcel.writeString(Description);
        parcel.writeString(otherOption);
        parcel.writeString(Caption);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }
}
