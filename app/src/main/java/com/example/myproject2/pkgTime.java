package com.example.myproject2;

import android.os.Parcel;
import android.os.Parcelable;


// class로 data 전송
public class pkgTime implements Parcelable {

    String pkgName;
    String s_dateTime;

    public pkgTime(){
    }

    public pkgTime(String pkgName, String dateTime){
        this.pkgName = pkgName;
        this.s_dateTime = dateTime;
    }

    protected pkgTime(Parcel in) {
        this.pkgName = in.readString();
        this.s_dateTime = in.readString();

    }

    public static final Creator<pkgTime> CREATOR = new Creator<pkgTime>() {
        @Override
        public pkgTime createFromParcel(Parcel in) {
            return new pkgTime(in);
        }

        @Override
        public pkgTime[] newArray(int size) {
            return new pkgTime[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pkgName);
        dest.writeString(this.s_dateTime);
    }


}
