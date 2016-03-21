package com.nghiepnguyen.survey.model.resultModel;

/**
 * Created by 08670_000 on 21/03/2016.
 */
public class QuestionnaireModel {
    private int ID;
    private int ParentID;
    private int ProjectID;
    private int Type;
    private int ZOrder;
    private int Status;
    private String Code;
    private String QuestionText;
    private String TypeCode;
    private String TypeName;

    public QuestionnaireModel() {
    }

    public QuestionnaireModel(int ID, int parentID, int projectID, int type, int ZOrder, int status, String code, String questionText, String typeCode, String typeName) {
        this.ID = ID;
        ParentID = parentID;
        ProjectID = projectID;
        Type = type;
        this.ZOrder = ZOrder;
        Status = status;
        Code = code;
        QuestionText = questionText;
        TypeCode = typeCode;
        TypeName = typeName;
    }

    @Override
    public String toString() {
        return "QuestionnaireModel{" +
                "ID=" + ID +
                ", ParentID=" + ParentID +
                ", ProjectID=" + ProjectID +
                ", Type=" + Type +
                ", ZOrder=" + ZOrder +
                ", Status=" + Status +
                ", Code='" + Code + '\'' +
                ", QuestionText='" + QuestionText + '\'' +
                ", TypeCode='" + TypeCode + '\'' +
                ", TypeName='" + TypeName + '\'' +
                '}';
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
    }

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getZOrder() {
        return ZOrder;
    }

    public void setZOrder(int ZOrder) {
        this.ZOrder = ZOrder;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
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
}
