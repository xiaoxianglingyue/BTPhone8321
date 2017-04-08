package com.xintu.smartcar.bluetoothphone.iface;

import java.util.ArrayList;


import android.os.Parcel;
import android.os.Parcelable;

public class SelectContactInfo implements Parcelable{
	public int count;
	
	public SelectContactInfo(){}
	
	public  ArrayList <CallInfo> selectContacts=new ArrayList<CallInfo>();

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		public SelectContactInfo(Parcel source) {
			readFromParcel(source);
		}
		
		public void readFromParcel(Parcel source) {
			int iIndexNum = source.readInt();
			for (int i=0; i<iIndexNum; i++) {
				CallInfo record = new CallInfo();
				record.m_strName = source.readString();
				record.m_strNumber = source.readString();
				selectContacts.add(record);
			}
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(selectContacts.size());
			for (int i=0; i<selectContacts.size(); i++) {
				CallInfo record = selectContacts.get(i);
				dest.writeString(record.m_strName);
				dest.writeString(record.m_strNumber);
			}
			
		}

		public static final Parcelable.Creator<SelectContactInfo> CREATOR = new Parcelable.Creator<SelectContactInfo>() { 

	        @Override 
	        public SelectContactInfo createFromParcel(Parcel source) { 
	                return new SelectContactInfo(source); 
	        } 

	        @Override 
	        public SelectContactInfo[] newArray(int size) { 
	                return new SelectContactInfo[size]; 
	        } 

		}; 				

}
