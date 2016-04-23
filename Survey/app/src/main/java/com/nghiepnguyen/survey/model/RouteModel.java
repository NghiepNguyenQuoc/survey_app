package com.nghiepnguyen.survey.model;

/**
 * Created by 08670_000 on 19/04/2016.
 */
public class RouteModel {
    private int QuestionnaireConditionsID;
    private int QuestionnaireID;
    private int ProjectID;
    private int Method;
    private int NextQuestionnaireID;
    private String ResponseValue;

    public int getQuestionnaireConditionsID() {
        return QuestionnaireConditionsID;
    }

    public void setQuestionnaireConditionsID(int questionnaireConditionsID) {
        QuestionnaireConditionsID = questionnaireConditionsID;
    }

    public int getQuestionnaireID() {
        return QuestionnaireID;
    }

    public void setQuestionnaireID(int questionnaireID) {
        QuestionnaireID = questionnaireID;
    }

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public int getMethod() {
        return Method;
    }

    public void setMethod(int method) {
        Method = method;
    }

    public int getNextQuestionnaireID() {
        return NextQuestionnaireID;
    }

    public void setNextQuestionnaireID(int nextQuestionnaireID) {
        NextQuestionnaireID = nextQuestionnaireID;
    }

    public String getResponseValue() {
        return ResponseValue;
    }

    public void setResponseValue(String responseValue) {
        ResponseValue = responseValue;
    }
}
