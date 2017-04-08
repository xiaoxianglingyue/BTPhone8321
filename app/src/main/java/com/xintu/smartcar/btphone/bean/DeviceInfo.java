package com.xintu.smartcar.btphone.bean;

import com.xintu.smartcar.btphone.utils.GlobalUtil;


public class DeviceInfo {
	public int m_iDeviceID;
	public String m_strMacAddr;
	public String m_strName;
	//2329000148746e793aef20201c006c0069007900750064006f006e0067201d76840020006900500068006f
	//232900
	//01
	//48746e793aef
	//20
	//201c006c0069007900750064006f006e0067201d76840020006900500068
	//006f
	
	//Parameters  Size  value  Parameter Description 
	//Status  1  0x00  Success 
	//    0x89  error_tdl_is_empty 
	//DeviceID  1    The device ID (location) in paired device list table 
	//stored in BT module��s internal flash memory. 
	//BdAddr  6     
	//SizeDevName  1  0x00 ~ 0x20   
	//DevName  N    The device name string could be UCS-2 format or 
	//UTF-8 format depending on customer requirement. 
	//ProfileMask  2    Remote device support profiles. It depend on device if 
	//discovery by service search.
	public DeviceInfo() {
		m_iDeviceID = 0;
		m_strMacAddr = null;
		m_strName= null;
	}
	public DeviceInfo(String strContent) {
		String strDeviceID = strContent.substring(6, 8);
		byte[] byteArray = GlobalUtil.decodeHex(strDeviceID.toCharArray());
		
		m_iDeviceID = (int)(byteArray[0] & 0xff);
		
		m_strMacAddr = strContent.substring(8, 20);
		String strNameLen = strContent.substring(20,22);
		byteArray = GlobalUtil.decodeHex(strNameLen.toCharArray());
		int iNameLen = byteArray[0] & 0xff;
		String strName = strContent.substring(22, 22 + iNameLen * 2);
		
		byte[] byteArrName = GlobalUtil.decodeHex(strName.toCharArray());
		
		m_strName = GlobalUtil.arrUCSToString(byteArrName);
		
	}
}
