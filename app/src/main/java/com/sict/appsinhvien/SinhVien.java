package com.sict.appsinhvien;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SinhVien implements Serializable {
    private String idCode;
    private String sinhVienID;
    private String fullName;
    private boolean sex;
    private String birthDay;
    private String phone;
    private String address;
    private String description;

    private String imageUrl ;
    private boolean isChecked;

    public SinhVien() {
    }


    public SinhVien(String idCode, String sinhVienID, String fullName, boolean sex, String birthDay, String phone, String address, String description, String imageUrl) {
        this.idCode = idCode;
        this.sinhVienID = sinhVienID;
        this.fullName = fullName;
        this.sex = sex;
        this.birthDay = birthDay;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.imageUrl = imageUrl;

    }

    public SinhVien(String idCode, String sinhVienID, String fullName, boolean sex, String birthDay, String phone, String address, String description, String imageUrl, boolean isChecked) {
        this.idCode = idCode;
        this.sinhVienID = sinhVienID;
        this.fullName = fullName;
        this.sex = sex;
        this.birthDay = birthDay;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isChecked = isChecked;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSinhVienID() {
        return sinhVienID;
    }

    public void setSinhVienID(String sinhVienID) {
        this.sinhVienID = sinhVienID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }
}

