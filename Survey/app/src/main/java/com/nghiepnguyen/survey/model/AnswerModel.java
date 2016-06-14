package com.nghiepnguyen.survey.model;

import java.util.List;
import java.util.Map;

/**
 * Created by nghiep on 5/3/16.
 */
public class AnswerModel {
    private int questionaireID;
    private List<SelectedOption> selectedOptions;

    public AnswerModel(int questionaireID, List<SelectedOption> selectedOptions) {
        this.questionaireID = questionaireID;
        this.selectedOptions = selectedOptions;
    }

    public int getQuestionaireID() {
        return questionaireID;
    }

    public void setQuestionaireID(int questionaireID) {
        this.questionaireID = questionaireID;
    }

    public List<SelectedOption> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<SelectedOption> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}
