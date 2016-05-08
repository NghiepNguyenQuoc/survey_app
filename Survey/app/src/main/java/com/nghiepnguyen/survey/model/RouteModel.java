package com.nghiepnguyen.survey.model;

/**
 * Created by 08670_000 on 19/04/2016.
 */
public class RouteModel {
    private int QuestionnaireConditionsID;
    private int QuestionnaireID_Check;
    private int QuestionnaireID_Check_Option;
    private int ProjectID;
    private int NextQuestionnaireID;
    private int Method;
    private int GroupMethod;
    private int GroupZOrder;
    private String ResponseValue;
    private String GroupMethodName;
    private String MethodName;

    public int getQuestionnaireConditionsID() {
        return QuestionnaireConditionsID;
    }

    public void setQuestionnaireConditionsID(int questionnaireConditionsID) {
        QuestionnaireConditionsID = questionnaireConditionsID;
    }

    public int getQuestionnaireID_Check() {
        return QuestionnaireID_Check;
    }

    public void setQuestionnaireID_Check(int questionnaireID_Check) {
        QuestionnaireID_Check = questionnaireID_Check;
    }

    public int getQuestionnaireID_Check_Option() {
        return QuestionnaireID_Check_Option;
    }

    public void setQuestionnaireID_Check_Option(int questionnaireID_Check_Option) {
        QuestionnaireID_Check_Option = questionnaireID_Check_Option;
    }

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public int getNextQuestionnaireID() {
        return NextQuestionnaireID;
    }

    public void setNextQuestionnaireID(int nextQuestionnaireID) {
        NextQuestionnaireID = nextQuestionnaireID;
    }

    public int getMethod() {
        return Method;
    }

    public void setMethod(int method) {
        Method = method;
    }

    public int getGroupMethod() {
        return GroupMethod;
    }

    public void setGroupMethod(int groupMethod) {
        GroupMethod = groupMethod;
    }

    public int getGroupZOrder() {
        return GroupZOrder;
    }

    public void setGroupZOrder(int groupZOrder) {
        GroupZOrder = groupZOrder;
    }

    public String getResponseValue() {
        return ResponseValue;
    }

    public void setResponseValue(String responseValue) {
        ResponseValue = responseValue;
    }

    public String getGroupMethodName() {
        return GroupMethodName;
    }

    public void setGroupMethodName(String groupMethodName) {
        GroupMethodName = groupMethodName;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }
}
