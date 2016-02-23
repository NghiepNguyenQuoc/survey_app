package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nghiep on 2/10/16.
 */
public class QuestionModel implements Parcelable {
    private int ID;
    private String QuestionText;
    private int ZOrder;
    private String Code;
    private int Type;
    private int MaxResponseCount;
    private String TypeCode;
    private String TypeName;

    public QuestionModel(Parcel in) {
        ID = in.readInt();
        QuestionText = in.readString();
        ZOrder = in.readInt();
        Code = in.readString();
        Type = in.readInt();
        MaxResponseCount = in.readInt();
        TypeCode = in.readString();
        TypeName = in.readString();
    }

    public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
        @Override
        public QuestionModel createFromParcel(Parcel in) {
            return new QuestionModel(in);
        }

        @Override
        public QuestionModel[] newArray(int size) {
            return new QuestionModel[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getQuestionText() {
        return QuestionText;
    }

    public void setQuestionText(String questionText) {
        QuestionText = questionText;
    }

    public int getZOrder() {
        return ZOrder;
    }

    public void setZOrder(int ZOrder) {
        this.ZOrder = ZOrder;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getMaxResponseCount() {
        return MaxResponseCount;
    }

    public void setMaxResponseCount(int maxResponseCount) {
        MaxResponseCount = maxResponseCount;
    }

    public String getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(String typeCode) {
        TypeCode = typeCode;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(QuestionText);
        parcel.writeInt(ZOrder);
        parcel.writeString(Code);
        parcel.writeInt(Type);
        parcel.writeInt(MaxResponseCount);
        parcel.writeString(TypeCode);
        parcel.writeString(TypeName);
    }
}
