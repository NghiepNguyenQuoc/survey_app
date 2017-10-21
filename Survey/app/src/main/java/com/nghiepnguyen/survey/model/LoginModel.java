package com.nghiepnguyen.survey.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 08670_000 on 28/02/2016.
 */
public class LoginModel {
    @SerializedName("Code")
    private String Code;
    @SerializedName("Name")
    private String Name;
    @SerializedName("Description")
    private String Description;
    @SerializedName("Result")
    private String Result;
    @SerializedName("IsSuccessfull")
    private boolean IsSuccessfull;
    @SerializedName("Member")
    private MemberModel Member;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public boolean isSuccessfull() {
        return IsSuccessfull;
    }

    public void setIsSuccessfull(boolean isSuccessfull) {
        IsSuccessfull = isSuccessfull;
    }

    public MemberModel getMember() {
        return Member;
    }

    public void setMember(MemberModel member) {
        Member = member;
    }
}
