package com.nghiepnguyen.survey.model;

/**
 * Created by nghiep on 3/1/16.
 */
public class CompletedProject {
    private String Code;
    private String Name;
    private String Description;
    private boolean IsSuccessfull;

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

    public boolean isSuccessfull() {
        return IsSuccessfull;
    }

    public void setIsSuccessfull(boolean isSuccessfull) {
        IsSuccessfull = isSuccessfull;
    }
}
