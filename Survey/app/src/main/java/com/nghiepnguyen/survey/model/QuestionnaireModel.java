package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 08670_000 on 21/03/2016.
 */
public class QuestionnaireModel implements Parcelable {
    private int ID;
    private int ProjectID;
    private int QuestionnaireID;
    private int DependentID;
    private int ParentID;
    private int Type;
    private int ZOrderQuestion;
    private int Value;
    private int AllowInputText;
    private int IsSelected;
    private int MaxResponseCount;
    private int Exclusion;
    private String Code;
    private String QuestionText;
    private String Caption;
    private String Description;
    private String ZOrderOption;
    private String otherOption;


    public QuestionnaireModel(Parcel in) {
        ID = in.readInt();
        ProjectID = in.readInt();
        QuestionnaireID = in.readInt();
        DependentID = in.readInt();
        ParentID = in.readInt();
        Type = in.readInt();
        ZOrderQuestion = in.readInt();
        Value = in.readInt();
        AllowInputText = in.readInt();
        IsSelected = in.readInt();
        MaxResponseCount = in.readInt();
        Exclusion = in.readInt();
        Code = in.readString();
        QuestionText = in.readString();
        Caption = in.readString();
        Description = in.readString();
        ZOrderOption = in.readString();
        otherOption = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(ProjectID);
        dest.writeInt(QuestionnaireID);
        dest.writeInt(DependentID);
        dest.writeInt(ParentID);
        dest.writeInt(Type);
        dest.writeInt(ZOrderQuestion);
        dest.writeInt(Value);
        dest.writeInt(AllowInputText);
        dest.writeInt(IsSelected);
        dest.writeInt(MaxResponseCount);
        dest.writeInt(Exclusion);
        dest.writeString(Code);
        dest.writeString(QuestionText);
        dest.writeString(Caption);
        dest.writeString(Description);
        dest.writeString(ZOrderOption);
        dest.writeString(otherOption);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public int getQuestionnaireID() {
        return QuestionnaireID;
    }

    public void setQuestionnaireID(int questionnaireID) {
        QuestionnaireID = questionnaireID;
    }

    public int getDependentID() {
        return DependentID;
    }

    public void setDependentID(int dependentID) {
        DependentID = dependentID;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
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

    public int getExclusion() {
        return Exclusion;
    }

    public void setExclusion(int exclusion) {
        Exclusion = exclusion;
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
}
