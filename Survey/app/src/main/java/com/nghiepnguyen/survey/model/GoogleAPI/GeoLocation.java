package com.nghiepnguyen.survey.model.GoogleAPI;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by W10-PRO on 14-Feb-17.
 */


public class GeoLocation implements Parcelable {

    private String primaryText;
    private String secondaryText;
    private String fullText;
    private String place_id;
    private String lat;
    private String lng;
    private String description;
    private String country_code;

    // use to save infomation of book again
    private String recipient_name;
    private String recipient_phone;
    private boolean isPayer;

    public void splitPrimaryText(String fullText, int offset) {
        if (offset > 0 && fullText.length() > offset) {
            this.primaryText = removeLastComma(fullText.substring(0, offset));
            this.secondaryText = removeFirstComma(removeLastComma(fullText.substring(offset)));
        } else {
            this.primaryText = fullText;
            this.secondaryText = "";
        }
    }

    // Remove last comma
    private String removeLastComma(String str) {
        str = str.trim();
        while (str.length() > 0 && str.substring(str.length() - 1).equals(","))
            str = str.substring(0, str.length() - 1).trim();
        return str;
    }

    // Remove first comma
    private String removeFirstComma(String str) {
        str = str.trim();
        while (str.length() > 0 && str.substring(0, 1).equals(","))
            str = str.substring(1).trim();
        return str;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public LatLng getLatLng() {
        try {
            return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        } catch (Exception e) {
            return null;
        }
    }


    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    // use to save infomation of book again
    public String getRecipient_name() {
        return recipient_name;
    }

    public void setRecipient_name(String recipient_name) {
        this.recipient_name = recipient_name;
    }

    public String getRecipient_phone() {
        return recipient_phone;
    }

    public void setRecipient_phone(String recipient_phone) {
        this.recipient_phone = recipient_phone;
    }

    public boolean isPayer() {
        return isPayer;
    }

    public void setIsPayer(boolean isPayer) {
        this.isPayer = isPayer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public String getFullText() {
        if (TextUtils.isEmpty(fullText)) {
            if (!TextUtils.isEmpty(primaryText) && !TextUtils.isEmpty(secondaryText)) {
                return (primaryText + ", " + secondaryText).trim();
            }
            if (!TextUtils.isEmpty(primaryText)) {
                return primaryText.trim();
            }
            return null;
        }
        return fullText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoLocation that = (GeoLocation) o;

        if (fullText != null ? !fullText.equals(that.fullText) : that.fullText != null)
            return false;
        if (place_id != null ? !place_id.equals(that.place_id) : that.place_id != null)
            return false;
        if (lat != null ? !lat.equals(that.lat) : that.lat != null) return false;
        return lng != null ? lng.equals(that.lng) : that.lng == null;

    }

    @Override
    public int hashCode() {
        int result = fullText != null ? fullText.hashCode() : 0;
        result = 31 * result + (place_id != null ? place_id.hashCode() : 0);
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (lng != null ? lng.hashCode() : 0);
        return result;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public boolean isNull() {
        return (getLatLng() == null && TextUtils.isEmpty(getFullText()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.primaryText);
        dest.writeString(this.secondaryText);
        dest.writeString(this.fullText);
        dest.writeString(this.place_id);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.description);
        dest.writeString(this.country_code);
        dest.writeString(this.recipient_name);
        dest.writeString(this.recipient_phone);
        dest.writeByte(this.isPayer ? (byte) 1 : (byte) 0);
    }

    public GeoLocation() {
    }

    protected GeoLocation(Parcel in) {
        this.primaryText = in.readString();
        this.secondaryText = in.readString();
        this.fullText = in.readString();
        this.place_id = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.description = in.readString();
        this.country_code = in.readString();
        this.recipient_name = in.readString();
        this.recipient_phone = in.readString();
        this.isPayer = in.readByte() != 0;
    }

    public static final Creator<GeoLocation> CREATOR = new Creator<GeoLocation>() {
        @Override
        public GeoLocation createFromParcel(Parcel source) {
            return new GeoLocation(source);
        }

        @Override
        public GeoLocation[] newArray(int size) {
            return new GeoLocation[size];
        }
    };
}
