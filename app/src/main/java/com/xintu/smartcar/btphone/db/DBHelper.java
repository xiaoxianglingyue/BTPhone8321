package com.xintu.smartcar.btphone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "btphone.db";  
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 3);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//创建联系人数据表
		db.execSQL("create table if not exists contacts (id  integer primary key autoincrement,number varchar(20),name varchar(20), namepinyin varchar(32))");
		//创建已经匹配好的蓝牙数据
		db.execSQL("create table if not exists btdevice (id  integer primary key autoincrement,devid integer, btname varchar(20),macaddress varchar(50))");
		//创建通话记录数据
		db.execSQL("create table if not exists callrecord (id  integer primary key autoincrement,flag integer,name varchar(20),number varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//db.execSQL("create table if not exists contacts (id  integer primary key autoincrement,number varchar(20),name varchar(20),mode integer, namepinyin varchar(32))");
	}

	/*public void free() {
		// TODO Auto-generated method stub
		
	}*/

}
