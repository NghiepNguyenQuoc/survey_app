package com.nghiepnguyen.survey.model;

import java.util.List;

/**
 * Created by nghiep on 6/6/16.
 */
public class SelectedOption {
    private int value;//value la key
    private String otherValue;// other option là text
    private String text;//ten cua option là text
    private int allowInputText;
    private List<AnswerModel> answerModelList;

    public SelectedOption(int value, String otherValue, String text, int allowInputText) {
        this.value = value;
        this.otherValue = otherValue;
        this.text = text;
        this.allowInputText = allowInputText;
    }

    public SelectedOption(List<AnswerModel> answerModelList) {
        this.answerModelList = answerModelList;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(String otherValue) {
        this.otherValue = otherValue;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAllowInputText() {
        return allowInputText;
    }

    public void setAllowInputText(int allowInputText) {
        this.allowInputText = allowInputText;
    }

    public List<AnswerModel> getAnswerModelList() {
        return answerModelList;
    }

    public void setAnswerModelList(List<AnswerModel> answerModelList) {
        this.answerModelList = answerModelList;
    }
}
