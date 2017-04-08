package com.xintu.smartcar.btphone.utils;

public class GetSystemVision {

	public static String getSystemVersion(){
		String version=android.os.Build.DISPLAY;
		if(version!=null&&version.contains("_")){
			String[] strs_version=version.split("_");
			if(strs_version.length>=4){
				String vs=strs_version[strs_version.length-2].replace("V", "");
				return vs;
			}
		}
		return "";
	}

}
