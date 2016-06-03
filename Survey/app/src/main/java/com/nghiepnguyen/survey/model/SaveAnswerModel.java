package com.nghiepnguyen.survey.model;

import java.util.Map;

/**
 * Created by nghiep on 5/3/16.
 */
public class SaveAnswerModel {
    private int identity;
    private String fullName;
    private String numberID;
    private String PhoneNumber;
    private String Address;
    private String Email;
    private int ProjectID;
    private int IsCompeleted;
    private String Data;

    public SaveAnswerModel() {
    }

    public SaveAnswerModel(String fullName, String numberID, String phoneNumber, String address, String email) {
        this.fullName = fullName;
        this.numberID = numberID;
        PhoneNumber = phoneNumber;
        Address = address;
        Email = email;
    }

    @Override
    public String toString() {
        return "SaveAnswerModel{" +
                "identity=" + identity +
                ", fullName='" + fullName + '\'' +
                ", numberID='" + numberID + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Address='" + Address + '\'' +
                ", Email='" + Email + '\'' +
                ", ProjectID=" + ProjectID +
                ", IsCompeleted=" + IsCompeleted +
                ", Data='" + Data + '\'' +
                '}';
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNumberID() {
        return numberID;
    }

    public void setNumberID(String numberID) {
        this.numberID = numberID;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public int getIsCompeleted() {
        return IsCompeleted;
    }

    public void setIsCompeleted(int isCompeleted) {
        IsCompeleted = isCompeleted;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}
