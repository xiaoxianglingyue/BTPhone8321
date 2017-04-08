package com.xintu.smartcar.btphone.db.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xintu.smartcar.bluetoothphone.iface.CallInfo;
import com.xintu.smartcar.bluetoothphone.iface.CallRecordInfo;
import com.xintu.smartcar.btphone.db.DBHelper;

public class CallRecordsDao {

	private DBHelper m_dbHelper;
	private String callrecord;
	private long time1;
	private long time2;
	public CallRecordsDao(Context context){
		m_dbHelper = new DBHelper(context);
	}

	//清空
	public void clearAll() {
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("delete from callrecord");
		db.close();
	}

	public void saveCallRecord(String name,String number,String currentTime)
	{
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		if(checkCount()>=50){
			clearAll();
		}
		db.execSQL("insert into  callrecord(name,number,currenttime) values(?,?,?)",new Object[]{name,number,currentTime});
		db.close();
	}

	public int checkCount(){
		SQLiteDatabase db=m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("callrecord", null,null,null, null, null, null);
		int count=cursor.getCount();
		cursor.close();
		db.close();
		return count;		
	}
	
	/**
	 * 查找
	 * @return List
	 */
	public CallRecordInfo findAll()
	{
		CallRecordInfo callRecordInfo=new CallRecordInfo();
		CallInfo callRecord=null;
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from callrecord", null);
		while(cursor.moveToNext())
		{
			callRecord=new CallInfo();
			
			callRecord.m_strName = cursor.getString(cursor.getColumnIndex("name"));
			callRecord.m_strNumber = cursor.getString(cursor.getColumnIndex("number"));
			callRecordInfo.callRecords.add(callRecord);
			Log.d("MOMOMO", "m_strName"+callRecord.m_strName);
		}
		
		db.close();
		return callRecordInfo;
	}

	
	public boolean findNumber(String number){
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		Cursor cursor=db.query("callrecord", null, "number = ?",new String[]{number}, null, null, null);
		if(cursor.moveToFirst()){
			return true;
		}
		cursor.close();
		db.close();
		return false;		
	}
	
	
	public ArrayList<String> findAllRecord()
	{
		String time;
		ArrayList<String> callRecordList=new ArrayList<String>();
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from callrecord", null);
		//select  callrecord(long,'2012-6-28 9:45:11',getdate())
		while(cursor.moveToNext())
		{   
			time=cursor.getString(cursor.getColumnIndex("currenttime"));
			callRecordList.add(time);
		}
		db.close();
		return callRecordList;
	}
	
	/**
	 * 根据电话类型查询
	 * @param number
	 * @return
	 */
	public ArrayList<CallInfo> findCallRecord(int flag){
		ArrayList<CallInfo> records=new ArrayList<CallInfo>();
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cursor=db.query("callrecord", new String[]{"name","number"}, "flag like ?", new String[]{flag+"%"}, null, null, null);
		while(cursor.moveToNext()){
			CallInfo callRecord=new CallInfo();
			callRecord.m_strName = cursor.getString(cursor.getColumnIndex("name"));
			callRecord.m_strNumber = cursor.getString(cursor.getColumnIndex("number"));
			records.add(callRecord);
		}
		cursor.close();
		db.close();
		return records;
	}

	public void clearFeedTable(){
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.delete("callrecord", null, null);
		revertSeq();
	}

	private void revertSeq() {
		String sql = "update sqlite_sequence set seq=0 where name='"+callrecord+"'";
		SQLiteDatabase db =  m_dbHelper.getWritableDatabase();
		db.execSQL(sql);
	}
	
	public boolean isExitNumber(String number){
		String strNumber="";
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		Cursor cur=db.query("callrecord", new String[]{"number"}, "number = ?", new String[]{number}, null, null, null);
		if(cur.moveToNext()){
			strNumber = cur.getString(cur.getColumnIndex("number"));
			if(!"".equals(strNumber)){
				return true;
			}
		}
		cur.close();
		db.close();
		return false;
	}

	public void delete(String time)
	{

		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		db.execSQL("delete from callrecord where currenttime=?", new Object[]{time});
		db.close();
	}
	
}
