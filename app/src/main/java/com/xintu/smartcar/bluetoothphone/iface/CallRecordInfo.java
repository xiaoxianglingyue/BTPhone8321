package com.xintu.smartcar.bluetoothphone.iface;


import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class CallRecordInfo implements Parcelable{

	
   public  ArrayList <CallInfo> callRecords=new ArrayList<CallInfo>();
   public CallRecordInfo(){}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public CallRecordInfo(Parcel source) {
		readFromParcel(source);
	}
	
	public void readFromParcel(Parcel source) {
		int iIndexNum = source.readInt();
		for (int i=0; i<iIndexNum; i++) {
			CallInfo record = new CallInfo();
			record.m_strName = source.readString();
			record.m_strNumber = source.readString();
			callRecords.add(record);
		}
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(callRecords.size());
		for (int i=0; i<callRecords.size(); i++) {
			CallInfo record = callRecords.get(i);
			dest.writeString(record.m_strName);
			dest.writeString(record.m_strNumber);
		}
		
	}

	public static final Parcelable.Creator<CallRecordInfo> CREATOR = new Parcelable.Creator<CallRecordInfo>() { 

        @Override 
        public CallRecordInfo createFromParcel(Parcel source) { 
                return new CallRecordInfo(source); 
        } 

        @Override 
        public CallRecordInfo[] newArray(int size) { 
                return new CallRecordInfo[size]; 
        } 

	}; 				
	
}
