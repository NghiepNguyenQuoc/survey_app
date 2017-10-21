package com.nghiepnguyen.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 08670_000 on 28/02/2016.
 */
public class MemberModel implements Parcelable {
    @SerializedName("ID")
    private int ID;
    @SerializedName("LoginName")
    private String LoginName;
    @SerializedName("SecrectToken")
    private String SecrectToken;
    @SerializedName("FirstName")
    private String FirstName;
    @SerializedName("LastName")
    private String LastName;
    @SerializedName("Email")
    private String Email;
    @SerializedName("CMND")
    private String CMND;
    @SerializedName("Phone")
    private String Phone;
    @SerializedName("Mobile")
    private String Mobile;
    @SerializedName("Type")
    private String Type;
    @SerializedName("TypeName")
    private String TypeName;
    @SerializedName("ClassName")
    private String ClassName;
    @SerializedName("Gender")
    private int Gender;
    @SerializedName("Mark")
    private int Mark;
    @SerializedName("DateOfBirth")
    private String DateOfBirth;
    @SerializedName("MaritalStatus")
    private int MaritalStatus;
    @SerializedName("MaritalStatusName")
    private String MaritalStatusName;
    @SerializedName("GenderName")
    private String GenderName;
    @SerializedName("JobID")
    private int JobID;
    @SerializedName("JobName")
    private String JobName;
    @SerializedName("Address")
    private String Address;
    @SerializedName("Street")
    private String Street;
    @SerializedName("StreetNo")
    private String StreetNo;
    @SerializedName("Ward")
    private String Ward;
    @SerializedName("ProvinceName")
    private String ProvinceName;
    @SerializedName("ProvinceID")
    private int ProvinceID;
    @SerializedName("EducationID")
    private int EducationID;
    @SerializedName("EducationName")
    private String EducationName;
    @SerializedName("UserGroupID")
    private int UserGroupID;
    @SerializedName("Avatar")
    private String Avatar;
    @SerializedName("Status")
    private int Status;
    @SerializedName("StatusName")
    private String StatusName;

    protected MemberModel(Parcel in) {
        ID = in.readInt();
        LoginName = in.readString();
        SecrectToken = in.readString();
        FirstName = in.readString();
        LastName = in.readString();
        Email = in.readString();
        CMND = in.readString();
        Phone = in.readString();
        Mobile = in.readString();
        Type = in.readString();
        TypeName = in.readString();
        ClassName = in.readString();
        Gender = in.readInt();
        Mark = in.readInt();
        DateOfBirth = in.readString();
        MaritalStatus = in.readInt();
        MaritalStatusName = in.readString();
        GenderName = in.readString();
        JobID = in.readInt();
        JobName = in.readString();
        Address = in.readString();
        Street = in.readString();
        StreetNo = in.readString();
        Ward = in.readString();
        ProvinceName = in.readString();
        ProvinceID = in.readInt();
        EducationID = in.readInt();
        EducationName = in.readString();
        UserGroupID = in.readInt();
        Avatar = in.readString();
        Status = in.readInt();
        StatusName = in.readString();
    }

    public static final Creator<MemberModel> CREATOR = new Creator<MemberModel>() {
        @Override
        public MemberModel createFromParcel(Parcel in) {
            return new MemberModel(in);
        }

        @Override
        public MemberModel[] newArray(int size) {
            return new MemberModel[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getSecrectToken() {
        return SecrectToken;
    }

    public void setSecrectToken(String secrectToken) {
        SecrectToken = secrectToken;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCMND() {
        return CMND;
    }

    public void setCMND(String CMND) {
        this.CMND = CMND;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public int getMark() {
        return Mark;
    }

    public void setMark(int mark) {
        Mark = mark;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public int getMaritalStatus() {
        return MaritalStatus;
    }

    public void setMaritalStatus(int maritalStatus) {
        MaritalStatus = maritalStatus;
    }

    public String getMaritalStatusName() {
        return MaritalStatusName;
    }

    public void setMaritalStatusName(String maritalStatusName) {
        MaritalStatusName = maritalStatusName;
    }

    public String getGenderName() {
        return GenderName;
    }

    public void setGenderName(String genderName) {
        GenderName = genderName;
    }

    public int getJobID() {
        return JobID;
    }

    public void setJobID(int jobID) {
        JobID = jobID;
    }

    public String getJobName() {
        return JobName;
    }

    public void setJobName(String jobName) {
        JobName = jobName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getStreetNo() {
        return StreetNo;
    }

    public void setStreetNo(String streetNo) {
        StreetNo = streetNo;
    }

    public String getWard() {
        return Ward;
    }

    public void setWard(String ward) {
        Ward = ward;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }

    public int getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(int provinceID) {
        ProvinceID = provinceID;
    }

    public int getEducationID() {
        return EducationID;
    }

    public void setEducationID(int educationID) {
        EducationID = educationID;
    }

    public String getEducationName() {
        return EducationName;
    }

    public void setEducationName(String educationName) {
        EducationName = educationName;
    }

    public int getUserGroupID() {
        return UserGroupID;
    }

    public void setUserGroupID(int userGroupID) {
        UserGroupID = userGroupID;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(LoginName);
        dest.writeString(SecrectToken);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(Email);
        dest.writeString(CMND);
        dest.writeString(Phone);
        dest.writeString(Mobile);
        dest.writeString(Type);
        dest.writeString(TypeName);
        dest.writeString(ClassName);
        dest.writeInt(Gender);
        dest.writeInt(Mark);
        dest.writeString(DateOfBirth);
        dest.writeInt(MaritalStatus);
        dest.writeString(MaritalStatusName);
        dest.writeString(GenderName);
        dest.writeInt(JobID);
        dest.writeString(JobName);
        dest.writeString(Address);
        dest.writeString(Street);
        dest.writeString(StreetNo);
        dest.writeString(Ward);
        dest.writeString(ProvinceName);
        dest.writeInt(ProvinceID);
        dest.writeInt(EducationID);
        dest.writeString(EducationName);
        dest.writeInt(UserGroupID);
        dest.writeString(Avatar);
        dest.writeInt(Status);
        dest.writeString(StatusName);
    }
}
