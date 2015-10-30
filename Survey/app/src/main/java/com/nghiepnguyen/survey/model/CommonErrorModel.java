package com.nghiepnguyen.survey.model;

/**
 * Created by nghiep on 10/29/15.
 */
public class CommonErrorModel {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================
    private int code;
    private String error;

    // ===========================================================
    // Constructors
    // ===========================================================
    public CommonErrorModel() {

    }

    public CommonErrorModel(int code, String error) {
        this.code = code;
        this.error = error;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
