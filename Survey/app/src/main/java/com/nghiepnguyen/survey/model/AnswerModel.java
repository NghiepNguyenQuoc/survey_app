package com.nghiepnguyen.survey.model;

import java.util.List;
import java.util.Map;

/**
 * Created by nghiep on 5/3/16.
 */
public class AnswerModel {
    private int questionaireID;
    private Map<Integer, String> arrText;// danh sach lưa chon, bao gom value la key, ten cua option là text.
    private Map<Integer, String> arrValue;// danh sach lưa chon, bao gom value la key, other option là text.

    public int getQuestionaireID() {
        return questionaireID;
    }

    public void setQuestionaireID(int questionaireID) {
        this.questionaireID = questionaireID;
    }

    public Map<Integer, String> getArrValue() {
        return arrValue;
    }

    public void setArrValue(Map<Integer, String> arrValue) {
        this.arrValue = arrValue;
    }

    public Map<Integer, String> getArrText() {
        return arrText;
    }

    public void setArrText(Map<Integer, String> arrText) {
        this.arrText = arrText;
    }

    public AnswerModel(int questionaireID, Map<Integer, String> arrValue) {
        this.questionaireID = questionaireID;
        this.arrValue = arrValue;
    }

    public AnswerModel(int questionaireID, Map<Integer, String> arrText, Map<Integer, String> arrValue) {
        this.questionaireID = questionaireID;
        this.arrText = arrText;
        this.arrValue = arrValue;
    }
}
