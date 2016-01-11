package com.project.waylay.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on : January 11, 2016
 * Author     : mnafian
 * Name       : M. Nafian
 * Email      : mnafian@icloud.com
 * GitHub     : https://github.com/mnafian
 * LinkedIn   : https://id.linkedin.com/in/mnafian
 */
public class CheckLicence implements Parcelable {

    private String status;

    protected CheckLicence(Parcel in) {
        status = in.readString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static final Creator<CheckLicence> CREATOR = new Creator<CheckLicence>() {
        @Override
        public CheckLicence createFromParcel(Parcel in) {
            return new CheckLicence(in);
        }

        @Override
        public CheckLicence[] newArray(int size) {
            return new CheckLicence[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
    }
}
