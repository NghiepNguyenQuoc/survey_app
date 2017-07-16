package com.nghiepnguyen.survey.model;

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
    private double GeoLatitude;
    private double GeoLongitude;
    private String GeoAddress;
    private String GeoTime;
    private String Data;
    private String StartRecordingTime;
    private String EndRecordingTime;

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
                ", GeoLatitude=" + GeoLatitude +
                ", GeoLongitude=" + GeoLongitude +
                ", GeoAddress='" + GeoAddress + '\'' +
                ", GeoTime='" + GeoTime + '\'' +
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

    public double getGeoLatitude() {
        return GeoLatitude;
    }

    public void setGeoLatitude(double geoLatitude) {
        GeoLatitude = geoLatitude;
    }

    public double getGeoLongitude() {
        return GeoLongitude;
    }

    public void setGeoLongitude(double geoLongitude) {
        GeoLongitude = geoLongitude;
    }

    public String getGeoAddress() {
        return GeoAddress;
    }

    public void setGeoAddress(String geoAddress) {
        GeoAddress = geoAddress;
    }

    public String getGeoTime() {
        return GeoTime;
    }

    public void setGeoTime(String geoTime) {
        GeoTime = geoTime;
    }

    public String getStartRecordingTime() {
        return StartRecordingTime;
    }

    public void setStartRecordingTime(String startRecordingTime) {
        StartRecordingTime = startRecordingTime;
    }

    public String getEndRecordingTime() {
        return EndRecordingTime;
    }

    public void setEndRecordingTime(String endRecordingTime) {
        EndRecordingTime = endRecordingTime;
    }
}
