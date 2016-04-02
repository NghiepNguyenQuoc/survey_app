package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 08670_000 on 21/03/2016.
 */
public class QuestionnaireModel implements Parcelable {
    private int ID;
    private int QuestionnaireID;
    private int Type;
    private int ZOrderQuestion;
    private int Value;
    private int AllowInputText;
    private int IsSelected;
    private int MaxResponseCount;
    private String Code;
    private String QuestionText;
    private String Caption;
    private String Description;
    private String ZOrderOption;
    private String otherOption;

    public QuestionnaireModel(Parcel in) {
        ID = in.readInt();
        QuestionnaireID = in.readInt();
        Type = in.readInt();
        ZOrderQuestion = in.readInt();
        Value = in.readInt();
        AllowInputText = in.readInt();
        IsSelected = in.readInt();
        MaxResponseCount = in.readInt();
        Code = in.readString();
        QuestionText = in.readString();
        Caption = in.readString();
        Description = in.readString();
        ZOrderOption = in.readString();
        otherOption = in.readString();
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

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getZOrderQuestion() {
        return ZOrderQuestion;
    }

    public void setZOrderQuestion(int ZOrderQuestion) {
        this.ZOrderQuestion = ZOrderQuestion;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public int getAllowInputText() {
        return AllowInputText;
    }

    public void setAllowInputText(int allowInputText) {
        AllowInputText = allowInputText;
    }

    public int getIsSelected() {
        return IsSelected;
    }

    public void setIsSelected(int isSelected) {
        IsSelected = isSelected;
    }

    public int getMaxResponseCount() {
        return MaxResponseCount;
    }

    public void setMaxResponseCount(int maxResponseCount) {
        MaxResponseCount = maxResponseCount;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getQuestionText() {
        return QuestionText;
    }

    public void setQuestionText(String questionText) {
        QuestionText = questionText;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getZOrderOption() {
        return ZOrderOption;
    }

    public void setZOrderOption(String ZOrderOption) {
        this.ZOrderOption = ZOrderOption;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(QuestionnaireID);
        dest.writeInt(Type);
        dest.writeInt(ZOrderQuestion);
        dest.writeInt(Value);
        dest.writeInt(AllowInputText);
        dest.writeInt(IsSelected);
        dest.writeInt(MaxResponseCount);
        dest.writeString(Code);
        dest.writeString(QuestionText);
        dest.writeString(Caption);
        dest.writeString(Description);
        dest.writeString(ZOrderOption);
        dest.writeString(otherOption);
    }
}
