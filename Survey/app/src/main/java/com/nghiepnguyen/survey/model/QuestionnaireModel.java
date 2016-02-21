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
    private String Description;
    private String Caption;

    protected QuestionnaireModel(Parcel in) {
        ID = in.readInt();
        QuestionnaireID = in.readInt();
        AllowInputText = in.readInt();
        Description = in.readString();
        Caption = in.readString();
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

    public int getAllowInputText() {
        return AllowInputText;
    }

    public void setAllowInputText(int allowInputText) {
        AllowInputText = allowInputText;
    }

    public int getQuestionnaireID() {
        return QuestionnaireID;
    }

    public void setQuestionnaireID(int questionnaireID) {
        QuestionnaireID = questionnaireID;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(QuestionnaireID);
        dest.writeInt(AllowInputText);
        dest.writeString(Description);
        dest.writeString(Caption);
    }
}
