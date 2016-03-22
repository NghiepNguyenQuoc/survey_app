package com.nghiepnguyen.survey.model.resultModel;

/**
 * Created by 08670_000 on 21/03/2016.
 */
public class QuestionnaireModel {
    private int ID;
    private String Code;
    private String QuestionText;
    private int ZOrder;
    private int Value;
    private String Caption;
    private String Description;

    public QuestionnaireModel() {
    }

    public QuestionnaireModel(int ID, String code, String questionText, int ZOrder, int value, String caption, String description) {
        this.ID = ID;
        Code = code;
        QuestionText = questionText;
        this.ZOrder = ZOrder;
        Value = value;
        Caption = caption;
        Description = description;
    }

    @Override
    public String toString() {
        return "QuestionnaireModel{" +
                "ID=" + ID +
                ", Code='" + Code + '\'' +
                ", QuestionText='" + QuestionText + '\'' +
                ", ZOrder=" + ZOrder +
                ", Value=" + Value +
                ", Caption='" + Caption + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }

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

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
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
}
