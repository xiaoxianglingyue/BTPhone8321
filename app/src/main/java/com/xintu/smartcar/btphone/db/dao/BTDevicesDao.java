package com.xintu.smartcar.btphone.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.xintu.smartcar.btphone.bean.DeviceInfo;
import com.xintu.smartcar.btphone.db.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BTDevicesDao {
	
	private DBHelper m_dbHelper;
	
	public BTDevicesDao(Context context){
		m_dbHelper = new DBHelper(context);
	}
	
	//清空
	public void clearAll() {
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("delete from btdevice");
		db.close();
	}
	
	public void saveBTDevice(int iDevID, String strName,String strMacAddr)
	{
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("insert into  btdevice(devid, btname,macaddress) values(?,?,?)",new Object[]{iDevID, strName, strMacAddr});
		db.close();
	}
	
	/**
	 * 查询已经配对好的设备
	 * @return List
	 */
	public List<DeviceInfo> findAll()
	{
		List<DeviceInfo> btDevicesList = new ArrayList<DeviceInfo>();
		DeviceInfo btDevice = null;
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from btdevice", null);
		while(cursor.moveToNext())
		{
			btDevice = new DeviceInfo();
			btDevice.m_iDeviceID = cursor.getInt(cursor.getColumnIndex("devid"));
			btDevice.m_strName = cursor.getString(cursor.getColumnIndex("btname"));
			btDevice.m_strMacAddr = cursor.getString(cursor.getColumnIndex("macaddress"));
			btDevicesList.add(btDevice);
		}
		db.close();
		return btDevicesList;
	}
	
	public boolean findDevice(String strParamMacAddr){
		boolean bFind = false;
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from btdevice where macaddress = ?", new String[] {strParamMacAddr});
		while(cursor.moveToNext()){
			String strCurMacAddr = cursor.getString(cursor.getColumnIndex("macaddress"));
			if (strCurMacAddr.equalsIgnoreCase(strParamMacAddr)) {
				bFind = true;
				break;
			}
		}
		cursor.close();
		db.close();
		
		return bFind;
	}
	
	public void delete(String strMac)
	{

		SQLiteDatabase db=m_dbHelper.getWritableDatabase();
		Log.d("MainActivity",strMac+"?????");
		//Log.d("MainActivity",deviceInfo.m_strName+">>>>");
		db.execSQL("delete from btdevice where macaddress=?", new Object[]{strMac});
		db.close();
	}
}
