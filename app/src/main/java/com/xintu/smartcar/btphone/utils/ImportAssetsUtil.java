package com.xintu.smartcar.btphone.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.xintu.smartcar.bluetoothphone.iface.CallInfo;
import com.xintu.smartcar.bluetoothphone.iface.SelectContactInfo;

import android.content.Context;
import android.util.Log;

public class ImportAssetsUtil {
	Context context;
	public ImportAssetsUtil(Context context){
		this.context=context;
	}
	//导入常用的电�?
	public List<String> getFromAssets(String fileName){
		List<String> listGeneral = new ArrayList<String>();
		try { 
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName) ); 
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line="";
			while((line = bufReader.readLine()) != null) {
				listGeneral.add(line);
			}

		} catch (Exception e) { 
			return null; 
		}
		return listGeneral;
	}

	public String importGeneralContact(String str,boolean isnumber) {
		List<String> listGeneral = getFromAssets("pb.txt");
		for (int i=0; i<listGeneral.size(); i++) {
			String strCurr = listGeneral.get(i);
			String[] strSplit = strCurr.split(",");
			if (strSplit.length == 2) {
				String strName = strSplit[0];
				String strNumber = strSplit[1];
				if(isnumber){
					if(str.equals(strNumber)){
						return strName+","+strNumber;
					}
				}else{
					if(strName.contains(str)){
						return strName+","+strNumber;
					}
				} 
			}
		}
		return "";
	}

	public ArrayList<CallInfo> importListGeneralContact(String str) {
		List<String> listGeneral = getFromAssets("pb.txt");	
		ArrayList<CallInfo> list=new ArrayList<CallInfo>();
		 
		for (int i=0; i<listGeneral.size(); i++) {
			CallInfo contact=new CallInfo();
			String strCurr = listGeneral.get(i);
			String[] strSplit = strCurr.split(",");
			if (strSplit.length == 2) {
				String strName = strSplit[0];
				String strNumber = strSplit[1];
				if(strName.contains(str)){
					contact.m_strName=strName;
					contact.m_strNumber=strNumber;
					list.add(contact);
				}
			} 
		}
		return list;
	}
}
