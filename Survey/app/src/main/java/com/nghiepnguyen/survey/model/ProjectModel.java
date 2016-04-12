package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nghiep on 11/22/15.
 */
public class ProjectModel implements Parcelable {
    private int ID;
    private String Name;
    private String Description;
    private String Image1;


    public ProjectModel(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
        Description = in.readString();
        Image1 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeString(Image1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProjectModel> CREATOR = new Creator<ProjectModel>() {
        @Override
        public ProjectModel createFromParcel(Parcel in) {
            return new ProjectModel(in);
        }

        @Override
        public ProjectModel[] newArray(int size) {
            return new ProjectModel[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    /*private int ID;
    private String Code;
    private String Name;
    private String Summary;
    private String Description;
    private String Image1;
    private String Image2;
    private String Image3;
    private String Image4;
    private String Tags;
    private String StartDate;
    private String EndDate;
    private int QuestionCount;
    private int Mark;
    private int UserGroupID;
    private String UserGroupName;
    private int Status;
    private int ZOrder;
    private int ResponseCount;
    private int ResponseCountFull;
    private boolean IsClosed;

    private boolean FullNameRequired;
    private boolean CMNDRequired;
    private boolean IssueDateRequired;
    private boolean IssuePlaceRequired;
    private boolean PhoneRequired;
    private boolean MobileRequired;
    private boolean TypeRequired;
    private boolean ClassRequired;
    private boolean DateOfBirthRequired;
    private boolean GenderRequired;
    private boolean IncomeHouseHoldsRequired;
    private boolean AddressRequired;
    private boolean EducationRequired;
    private boolean JobIDRequired;
    private boolean MaritalStatusRequired;
    private boolean IDRequired;
    private boolean ProvinceIDRequired;
    private boolean PersonalIncomeRequired;
    private boolean HouseholdIncomeRequired;
    private boolean DistrictRequired;
    private boolean WardRequired;

    public ProjectModel(Parcel in) {
        ID = in.readInt();
        Code = in.readString();
        Name = in.readString();
        Summary = in.readString();
        Description = in.readString();
        Image1 = in.readString();
        Image2 = in.readString();
        Image3 = in.readString();
        Image4 = in.readString();
        Tags = in.readString();
        StartDate = in.readString();
        EndDate = in.readString();
        QuestionCount = in.readInt();
        Mark = in.readInt();
        UserGroupID = in.readInt();
        UserGroupName = in.readString();
        Status = in.readInt();
        ZOrder = in.readInt();
        ResponseCount = in.readInt();
        ResponseCountFull = in.readInt();
        IsClosed = in.readByte() != 0;
        FullNameRequired = in.readByte() != 0;
        CMNDRequired = in.readByte() != 0;
        IssueDateRequired = in.readByte() != 0;
        IssuePlaceRequired = in.readByte() != 0;
        PhoneRequired = in.readByte() != 0;
        MobileRequired = in.readByte() != 0;
        TypeRequired = in.readByte() != 0;
        ClassRequired = in.readByte() != 0;
        DateOfBirthRequired = in.readByte() != 0;
        GenderRequired = in.readByte() != 0;
        IncomeHouseHoldsRequired = in.readByte() != 0;
        AddressRequired = in.readByte() != 0;
        EducationRequired = in.readByte() != 0;
        JobIDRequired = in.readByte() != 0;
        MaritalStatusRequired = in.readByte() != 0;
        IDRequired = in.readByte() != 0;
        ProvinceIDRequired = in.readByte() != 0;
        PersonalIncomeRequired = in.readByte() != 0;
        HouseholdIncomeRequired = in.readByte() != 0;
        DistrictRequired = in.readByte() != 0;
        WardRequired = in.readByte() != 0;
    }

    public static final Creator<ProjectModel> CREATOR = new Creator<ProjectModel>() {
        @Override
        public ProjectModel createFromParcel(Parcel in) {
            return new ProjectModel(in);
        }

        @Override
        public ProjectModel[] newArray(int size) {
            return new ProjectModel[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

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

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getImage3() {
        return Image3;
    }

    public void setImage3(String image3) {
        Image3 = image3;
    }

    public String getImage4() {
        return Image4;
    }

    public void setImage4(String image4) {
        Image4 = image4;
    }

    public String getTags() {
        return Tags;
    }

    public void setTags(String tags) {
        Tags = tags;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public int getQuestionCount() {
        return QuestionCount;
    }

    public void setQuestionCount(int questionCount) {
        QuestionCount = questionCount;
    }

    public int getMark() {
        return Mark;
    }

    public void setMark(int mark) {
        Mark = mark;
    }

    public int getUserGroupID() {
        return UserGroupID;
    }

    public void setUserGroupID(int userGroupID) {
        UserGroupID = userGroupID;
    }

    public String getUserGroupName() {
        return UserGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        UserGroupName = userGroupName;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getZOrder() {
        return ZOrder;
    }

    public void setZOrder(int ZOrder) {
        this.ZOrder = ZOrder;
    }

    public int getResponseCount() {
        return ResponseCount;
    }

    public void setResponseCount(int responseCount) {
        ResponseCount = responseCount;
    }

    public int getResponseCountFull() {
        return ResponseCountFull;
    }

    public void setResponseCountFull(int responseCountFull) {
        ResponseCountFull = responseCountFull;
    }

    public boolean isClosed() {
        return IsClosed;
    }

    public void setIsClosed(boolean isClosed) {
        IsClosed = isClosed;
    }

    public boolean isFullNameRequired() {
        return FullNameRequired;
    }

    public void setFullNameRequired(boolean fullNameRequired) {
        FullNameRequired = fullNameRequired;
    }

    public boolean isCMNDRequired() {
        return CMNDRequired;
    }

    public void setCMNDRequired(boolean CMNDRequired) {
        this.CMNDRequired = CMNDRequired;
    }

    public boolean issueDateRequired() {
        return IssueDateRequired;
    }

    public void setIssueDateRequired(boolean issueDateRequired) {
        IssueDateRequired = issueDateRequired;
    }

    public boolean issuePlaceRequired() {
        return IssuePlaceRequired;
    }

    public void setIssuePlaceRequired(boolean issuePlaceRequired) {
        IssuePlaceRequired = issuePlaceRequired;
    }

    public boolean isPhoneRequired() {
        return PhoneRequired;
    }

    public void setPhoneRequired(boolean phoneRequired) {
        PhoneRequired = phoneRequired;
    }

    public boolean isMobileRequired() {
        return MobileRequired;
    }

    public void setMobileRequired(boolean mobileRequired) {
        MobileRequired = mobileRequired;
    }

    public boolean isTypeRequired() {
        return TypeRequired;
    }

    public void setTypeRequired(boolean typeRequired) {
        TypeRequired = typeRequired;
    }

    public boolean isClassRequired() {
        return ClassRequired;
    }

    public void setClassRequired(boolean classRequired) {
        ClassRequired = classRequired;
    }

    public boolean isDateOfBirthRequired() {
        return DateOfBirthRequired;
    }

    public void setDateOfBirthRequired(boolean dateOfBirthRequired) {
        DateOfBirthRequired = dateOfBirthRequired;
    }

    public boolean isGenderRequired() {
        return GenderRequired;
    }

    public void setGenderRequired(boolean genderRequired) {
        GenderRequired = genderRequired;
    }

    public boolean isIncomeHouseHoldsRequired() {
        return IncomeHouseHoldsRequired;
    }

    public void setIncomeHouseHoldsRequired(boolean incomeHouseHoldsRequired) {
        IncomeHouseHoldsRequired = incomeHouseHoldsRequired;
    }

    public boolean isAddressRequired() {
        return AddressRequired;
    }

    public void setAddressRequired(boolean addressRequired) {
        AddressRequired = addressRequired;
    }

    public boolean isEducationRequired() {
        return EducationRequired;
    }

    public void setEducationRequired(boolean educationRequired) {
        EducationRequired = educationRequired;
    }

    public boolean isJobIDRequired() {
        return JobIDRequired;
    }

    public void setJobIDRequired(boolean jobIDRequired) {
        JobIDRequired = jobIDRequired;
    }

    public boolean isMaritalStatusRequired() {
        return MaritalStatusRequired;
    }

    public void setMaritalStatusRequired(boolean maritalStatusRequired) {
        MaritalStatusRequired = maritalStatusRequired;
    }

    public boolean isIDRequired() {
        return IDRequired;
    }

    public void setIDRequired(boolean IDRequired) {
        this.IDRequired = IDRequired;
    }

    public boolean isProvinceIDRequired() {
        return ProvinceIDRequired;
    }

    public void setProvinceIDRequired(boolean provinceIDRequired) {
        ProvinceIDRequired = provinceIDRequired;
    }

    public boolean isPersonalIncomeRequired() {
        return PersonalIncomeRequired;
    }

    public void setPersonalIncomeRequired(boolean personalIncomeRequired) {
        PersonalIncomeRequired = personalIncomeRequired;
    }

    public boolean isHouseholdIncomeRequired() {
        return HouseholdIncomeRequired;
    }

    public void setHouseholdIncomeRequired(boolean householdIncomeRequired) {
        HouseholdIncomeRequired = householdIncomeRequired;
    }

    public boolean isDistrictRequired() {
        return DistrictRequired;
    }

    public void setDistrictRequired(boolean districtRequired) {
        DistrictRequired = districtRequired;
    }

    public boolean isWardRequired() {
        return WardRequired;
    }

    public void setWardRequired(boolean wardRequired) {
        WardRequired = wardRequired;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(Code);
        parcel.writeString(Name);
        parcel.writeString(Summary);
        parcel.writeString(Description);
        parcel.writeString(Image1);
        parcel.writeString(Image2);
        parcel.writeString(Image3);
        parcel.writeString(Image4);
        parcel.writeString(Tags);
        parcel.writeString(StartDate);
        parcel.writeString(EndDate);
        parcel.writeInt(QuestionCount);
        parcel.writeInt(Mark);
        parcel.writeInt(UserGroupID);
        parcel.writeString(UserGroupName);
        parcel.writeInt(Status);
        parcel.writeInt(ZOrder);
        parcel.writeInt(ResponseCount);
        parcel.writeInt(ResponseCountFull);
        parcel.writeByte((byte) (IsClosed ? 1 : 0));
        parcel.writeByte((byte) (FullNameRequired ? 1 : 0));
        parcel.writeByte((byte) (CMNDRequired ? 1 : 0));
        parcel.writeByte((byte) (IssueDateRequired ? 1 : 0));
        parcel.writeByte((byte) (IssuePlaceRequired ? 1 : 0));
        parcel.writeByte((byte) (PhoneRequired ? 1 : 0));
        parcel.writeByte((byte) (MobileRequired ? 1 : 0));
        parcel.writeByte((byte) (TypeRequired ? 1 : 0));
        parcel.writeByte((byte) (ClassRequired ? 1 : 0));
        parcel.writeByte((byte) (DateOfBirthRequired ? 1 : 0));
        parcel.writeByte((byte) (GenderRequired ? 1 : 0));
        parcel.writeByte((byte) (IncomeHouseHoldsRequired ? 1 : 0));
        parcel.writeByte((byte) (AddressRequired ? 1 : 0));
        parcel.writeByte((byte) (EducationRequired ? 1 : 0));
        parcel.writeByte((byte) (JobIDRequired ? 1 : 0));
        parcel.writeByte((byte) (MaritalStatusRequired ? 1 : 0));
        parcel.writeByte((byte) (IDRequired ? 1 : 0));
        parcel.writeByte((byte) (ProvinceIDRequired ? 1 : 0));
        parcel.writeByte((byte) (PersonalIncomeRequired ? 1 : 0));
        parcel.writeByte((byte) (HouseholdIncomeRequired ? 1 : 0));
        parcel.writeByte((byte) (DistrictRequired ? 1 : 0));
        parcel.writeByte((byte) (WardRequired ? 1 : 0));
    }*/
}
