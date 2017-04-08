package com.xintu.smartcar.bluetoothphone.iface;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrentCall implements Parcelable{
	public String callName;
	public String callNumber;
	
	public CurrentCall(){}
	
	public CurrentCall(Parcel source) {
		readToParcel(source);
	}

	public void readToParcel(Parcel source) {
		callName=source.readString();
		callNumber=source.readString();
	}
	
	public void writeToParcel(Parcel _data, int i) {
		_data.writeString(callName);
		_data.writeString(callNumber);
	}
	
	public static final Parcelable.Creator<CurrentCall> CREATOR = new Parcelable.Creator<CurrentCall>() { 

        @Override 
        public CurrentCall createFromParcel(Parcel source) { 
                return new CurrentCall(source); 
        } 

        @Override 
        public CurrentCall[] newArray(int size) { 
                return new CurrentCall[size]; 
        } 

	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
